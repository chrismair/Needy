/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dx42.needy.parser

import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.dx42.needy.Dependency
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import groovy.transform.Canonical

/**
 * DependencyParser for Gradle Groovy-based DSL files
 *
 * @author Chris Mair
 */
class GradleDependencyParser implements DependencyParser {

    private static final Logger LOG = LoggerFactory.getLogger(GradleDependencyParser)

    @SuppressWarnings('MissingBlankLineAfterImports')
    private static final String STANDARD_GRADLE_API_IMPORTS = """
        import org.gradle.api.*
        import org.gradle.api.file.*
        import org.gradle.api.tasks.*
    """

    boolean includeFileDependencies = true

    @Override
    List<Dependency> parse(String applicationName, String source, Map<String, Object> binding) {
        if (source == null) {
            throw new IllegalArgumentException("Parameter source was null")
        }

        def dslContext = new DslContext(applicationName, binding, includeFileDependencies)

        GroovyShell shell = createGroovyShell(dslContext, binding)
        try {
            String normalizedSource = STANDARD_GRADLE_API_IMPORTS + source
            shell.evaluate(normalizedSource)
        }
        catch (MultipleCompilationErrorsException compileError) {
            LOG.error("An error occurred compiling: [$source]", compileError)
            throw new IllegalStateException("An error occurred compiling: [$source]\n${compileError.message}")
        }

        List<Dependency> dependencies = dslContext.dependencies
        LOG.info "dependencies = $dependencies"
        return dependencies
    }

    private GroovyShell createGroovyShell(DslContext dslContext, Map<String, Object> binding) {
        DependenciesDslEvaluator dependenciesDslEvaluator = new DependenciesDslEvaluator(dslContext)
        BuildscriptDslEvaluator buildscriptDslEvaluator = new BuildscriptDslEvaluator(dslContext)

        def callDependencies = { Closure closure -> dependenciesDslEvaluator.evaluate(closure) }
        def callBuildscript = { Closure closure -> buildscriptDslEvaluator.evaluate(closure) }

        Map bindingMap = [dependencies:callDependencies, buildscript:callBuildscript] + binding
        bindingMap = bindingMap.withDefault { name -> return DoNothing.INSTANCE }

        Binding groovyShellBinding = new Binding(bindingMap)

        return new GroovyShell(this.class.classLoader, groovyShellBinding)
    }
}

@Canonical
class DslContext {
    String applicationName
    Map<String, Object> binding
    boolean includeFileDependencies
    List<Dependency> dependencies = []
}

class FileDependency {
    protected static final String GROUP = "** FILE DEPENDENCY"

    String name
    Object[] args
}

class BuildscriptDslEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(BuildscriptDslEvaluator)

    final DslContext dslContext

    BuildscriptDslEvaluator(DslContext dslContext) {
        this.dslContext = dslContext
    }

    void dependencies(Closure closure) {
        DependenciesDslEvaluator dslEvaluator = new DependenciesDslEvaluator(dslContext)
        dslEvaluator.evaluate(closure)
    }

    // Handles syntax such as: dependencies.classpath 'org.jfrog.buildinfo:other-stuff:4.5.2'
    Map getDependencies() {
        return [classpath:{ artifact -> dslContext.dependencies << ParseUtil.createDependencyFromString(dslContext.applicationName, "classpath", artifact) }]
    }

    @SuppressWarnings('MethodParameterTypeRequired')
    Object methodMissing(String name, args) {
        LOG.info("methodMissing: name=$name, args=$args")
    }

    void evaluate(Closure closure) {
        closure.delegate = this
        closure.setResolveStrategy(Closure.DELEGATE_FIRST)
        closure.call()
    }
}

@SuppressWarnings('Instanceof')
class DependenciesDslEvaluator {

    private static final Logger LOG = LoggerFactory.getLogger(DependenciesDslEvaluator)

    final DslContext dslContext
    final List ignoredMethodNames = ['project']

    DependenciesDslEvaluator(DslContext dslContext) {
        this.dslContext = dslContext

        this.ignoredMethodNames = ['project']
        if (!dslContext.includeFileDependencies) {
            this.ignoredMethodNames += ["files", "fileTree"]
        }
    }

    void evaluate(Closure closure) {
        closure.delegate = this
        closure.setResolveStrategy(Closure.DELEGATE_FIRST)
        closure.call()
    }

    @SuppressWarnings('MethodParameterTypeRequired')
    Object methodMissing(String name, args) {
        if (ignoredMethodNames.contains(name)) {
            return
        }
        if (name in ["files", "fileTree"]) {
            def actualArgs = (args[-1] instanceof Closure) ? args[0..-2] : args
            return new FileDependency(name:name, args:actualArgs)
        }
        if (args.length == 0) {
            return
        }
        if (args[0] instanceof FileDependency) {
            String methodString = args[0].name + "(" + args[0].args.inspect() + ")"
            dslContext.dependencies << new Dependency(applicationName:dslContext.applicationName, group:FileDependency.GROUP, name:methodString, version:"", configuration:name)
        }
        if (args[0] instanceof Map) {
            LOG.info "methodMissing (Map): name=$name value=${args[0]}"
            args.each { arg ->
                if (arg instanceof Map) {
                    dslContext.dependencies << new Dependency(applicationName:dslContext.applicationName, group:arg.group, name:arg.name, version:arg.version, configuration:name)
                }
            }
        }
        else if (args[0] instanceof List) {
            LOG.info "methodMissing (List): name=$name value=${args[0]}"
            for (List list: args) {
                for (String s: list) {
                    dslContext.dependencies << ParseUtil.createDependencyFromString(dslContext.applicationName, name, s)
                }
            }
        }
        else {
            int index = 0
            while (index < args.length && args[index] instanceof CharSequence) {
                String s = args[index]
                LOG.info "methodMissing: name=$name value=${s}"
                dslContext.dependencies << ParseUtil.createDependencyFromString(dslContext.applicationName, name, s)
                index++
            }
        }
    }

    Object propertyMissing(String name) {
        def bindingValue = dslContext.binding.containsKey(name) ? dslContext.binding[name] : DoNothing.INSTANCE
        LOG.info("propertyMissing: $name; value=$bindingValue")
        return bindingValue
    }
}
