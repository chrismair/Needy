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
 * DependencyParser for Grails 2.x "BuildConfig.groovy" files.
 *
 * @author Chris Mair
 */
class GrailsBuildConfigDependencyParser implements DependencyParser {

	private static final Logger LOG = LoggerFactory.getLogger(GrailsBuildConfigDependencyParser)

	@Override
	List<Dependency> parse(String applicationName, String source) {
		if (source == null) {
			throw new IllegalArgumentException("Parameter closure was null")
		}

		GrailsBuildConfig_DslEvaluator dslEvaluator = new GrailsBuildConfig_DslEvaluator(applicationName)

		def grailsMap = [
			project:[
				dependency:[:]	// the "grails.project.dependency.resolution" closure will be stored in here
			].withDefault(ParseUtil.ignoreEverything())
		].withDefault(ParseUtil.ignoreEverything())

		GroovyShell shell = createGroovyShell(grailsMap)
		try {
			shell.evaluate(source)
		} 
		catch (MultipleCompilationErrorsException compileError) {
			LOG.error("An error occurred compiling: [$source]", compileError)
			throw new IllegalStateException("An error occurred compiling: [$source]\n${compileError.message}")
		}

		def project = grailsMap.project
		def dependency = project.dependency
		Closure resolutionClosure = dependency['resolution']
		
		if (resolutionClosure) {
			dslEvaluator.evaluate(resolutionClosure)
		}
		
		List<Dependency> dependencies = dslEvaluator.dependencies
		LOG.info "dependencies = $dependencies"
		return dependencies
	}
	
	private GroovyShell createGroovyShell(def grailsMap) {
		Map bindingMap = [
			grails:grailsMap,
			userHome:'/home/chris',							// TODO Remove and make this configurable
			].withDefault(ParseUtil.ignoreEverything())
		Binding binding = new Binding(bindingMap)

		return new GroovyShell(this.class.classLoader, binding)
	}
	
}

class GrailsBuildConfig_DslEvaluator {
	
	private static final Logger LOG = LoggerFactory.getLogger(GrailsBuildConfig_DslEvaluator)
	
	final List<Dependency> dependencies = []
	final String applicationName
	
	GrailsBuildConfig_DslEvaluator(String applicationName) {
		this.applicationName = applicationName
	}
	
	void evaluate(Closure closure) {
		closure.delegate = this
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure.call()
	}
	
	def methodMissing(String name, args) {
		if (name == "dependencies" && args.size() == 1 && args[0] instanceof Closure) {
			Closure dependenciesClosure = args[0]
			def dependenciesDslEvaluator = new GrailsBuildConfig_DependenciesClosure_DslEvaluator(applicationName)
			dependenciesDslEvaluator.evaluate(dependenciesClosure)
			LOG.info("Found: ${dependenciesDslEvaluator.dependencies}")
			dependencies.addAll(dependenciesDslEvaluator.dependencies)
			return
		}
		LOG.info("** ignoring [$name]")
		return
	}
	
	def propertyMissing(String name) {
		LOG.info("propertyMissing: $name")
		return [:]
	}
}

class GrailsBuildConfig_DependenciesClosure_DslEvaluator {

	private static final Logger LOG = LoggerFactory.getLogger(GrailsBuildConfig_DependenciesClosure_DslEvaluator)
	
	final List<Dependency> dependencies = []
	final String applicationName
	
	GrailsBuildConfig_DependenciesClosure_DslEvaluator(String applicationName) {
		this.applicationName = applicationName
	}
	
	void evaluate(Closure closure) {
		closure.delegate = this
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure.call()
	}

	def methodMissing(String name, args) {
		if (isValidConfigurationName(name)) {
			LOG.info("Processing [$name]")
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
		else {
			throw new IllegalArgumentException("Illegal configuration name [$name]")
		}
	}

	def propertyMissing(String name) {
		LOG.info("propertyMissing: $name") 
		return [:] 
	}
	
	private boolean isValidConfigurationName(String name) {
		return name in ['compile', 'test', 'runtime', 'build', 'provided']
	}
	
}
