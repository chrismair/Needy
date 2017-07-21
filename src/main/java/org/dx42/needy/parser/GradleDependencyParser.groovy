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

/**
 * DependencyParser for Gradle Groovy-based DSL files
 *
 * @author Chris Mair
 */
class GradleDependencyParser implements DependencyParser {

	private static final Logger LOG = LoggerFactory.getLogger(GradleDependencyParser)

	private static final String STANDARD_GRADLE_API_IMPORTS = """
		import org.gradle.api.file.*
	"""
	
	@Override
	List<Dependency> parse(String applicationName, String source, Map<String, Object> binding) {
		if (source == null) {
			throw new IllegalArgumentException("Parameter source was null")
		}

		GradleDependencyParser_DslEvaluator dslEvaluator = new GradleDependencyParser_DslEvaluator(applicationName, binding)
		
		GroovyShell shell = createGroovyShell(dslEvaluator, binding)
		try {
			String normalizedSource = STANDARD_GRADLE_API_IMPORTS + source
			shell.evaluate(normalizedSource)
		} 
		catch (MultipleCompilationErrorsException compileError) {
			LOG.error("An error occurred compiling: [$source]", compileError)
			throw new IllegalStateException("An error occurred compiling: [$source]\n${compileError.message}")
		}

		List<Dependency> dependencies = dslEvaluator.dependencies
		LOG.info "dependencies = $dependencies"
		return dependencies
	}

	private GroovyShell createGroovyShell(GradleDependencyParser_DslEvaluator dslEvaluator, Map<String, Object> binding) {
		def callDependencies = { Closure closure ->
			dslEvaluator.evaluate(closure)
		}
		Map predefinedMap = [dependencies:callDependencies] + binding
		Map bindingMap = predefinedMap.withDefault { name -> return DoNothing.INSTANCE }
		Binding groovyShellBinding = new Binding(bindingMap)

		return new GroovyShell(this.class.classLoader, groovyShellBinding)
	}
	
}

class GradleDependencyParser_DslEvaluator {

	private static final Logger LOG = LoggerFactory.getLogger(GradleDependencyParser_DslEvaluator)
	private static final IGNORED_METHOD_NAMES = ['project', 'files', 'fileTree']
	
	final List<Dependency> dependencies = []
	final String applicationName
	final Map<String, Object> binding
	
	GradleDependencyParser_DslEvaluator(String applicationName, Map<String, Object> binding) {
		this.applicationName = applicationName
		this.binding = binding
	}
	
	void evaluate(Closure closure) {
		closure.delegate = this
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure.call()
	}

	def methodMissing(String name, args) {
		if (IGNORED_METHOD_NAMES.contains(name)) {
			return
		}
		if (args.length == 0) {
			return
		}
		if (args[0] instanceof Map) {
			LOG.info "methodMissing (Map): name=$name value=${args[0]}"
			for (Map m: args) {
				dependencies << new Dependency(applicationName:applicationName, group:m.group, name:m.name, version:m.version, configuration:name)
			}
		}
		else if (args[0] instanceof List) {
			LOG.info "methodMissing (List): name=$name value=${args[0]}"
			for (List list: args) {
				for (String s: list) {
					dependencies << ParseUtil.createDependencyFromString(applicationName, name, s)
				}
			}
		}
		else {
			int index = 0
			while (index < args.length && args[index] instanceof CharSequence) {
				String s = args[index]
				LOG.info "methodMissing: name=$name value=${s}"
				dependencies << ParseUtil.createDependencyFromString(applicationName, name, s)
				index++
			}
		}
	}

	def propertyMissing(String name) {
		def bindingValue = binding.containsKey(name) ? binding[name] : DoNothing.INSTANCE
		LOG.info("propertyMissing: $name; value=$bindingValue") 
		return bindingValue
	}
	
}
