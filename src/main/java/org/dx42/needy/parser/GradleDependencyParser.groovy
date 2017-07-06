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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.dx42.needy.Dependency

class GradleDependencyParser implements DependencyParser {

	private static final Logger LOG = LoggerFactory.getLogger(GradleDependencyParser)

	private static final String STANDARD_GRADLE_API_IMPORTS = """
		import org.gradle.api.file.*
	"""
	
	final String applicationName
	
	GradleDependencyParser(String applicationName) {
		this.applicationName = applicationName
	}
	
	List<Dependency> parse(String source) {
		if (source == null) {
			throw new IllegalArgumentException("Parameter closure was null")
		}

		GradleDependencyParser_DslEvaluator dslEvaluator = new GradleDependencyParser_DslEvaluator(applicationName)
		
		GroovyShell shell = createGroovyShell(dslEvaluator)
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

	def methodMissing(String name, args) {
		LOG.info("MAIN methodMissing: $name; args=$args") 
	}
	
	def propertyMissing(String name) {
		LOG.info("MAIN propertyMissing: $name") 
		return [:] 
	}
	
	private GroovyShell createGroovyShell(GradleDependencyParser_DslEvaluator dslEvaluator) {
		def callDependencies = { Closure closure ->
			dslEvaluator.evaluate(closure)
		}
		Map bindingMap = [dependencies:callDependencies, ext:[:]].withDefault { name ->
			return DoNothing.INSTANCE 
		}
		Binding binding = new Binding(bindingMap)

		return new GroovyShell(this.class.classLoader, binding)
	}
	
}

class GradleDependencyParser_DslEvaluator {

	private static final Logger LOG = LoggerFactory.getLogger(GradleDependencyParser_DslEvaluator)
	private static final IGNORED_METHOD_NAMES = ['project', 'files', 'fileTree']
	
	final List<Dependency> dependencies = []
	final String applicationName
	
	GradleDependencyParser_DslEvaluator(String applicationName) {
		this.applicationName = applicationName
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
					addDependencyFromString(s, name)
				}
			}
		}
		else {
			int index = 0
			while (index < args.length && args[index] instanceof CharSequence) {
				String s = args[index]
				LOG.info "methodMissing: name=$name value=${s}"
				addDependencyFromString(s, name)
				index++
			}
		}
	}

	def propertyMissing(String name) {
		LOG.info("propertyMissing: $name") 
		return [:] 
	}
	
	private void addDependencyFromString(String s, String name) {
		assert s.size() > 0, "String format dependency error - empty string"
		s = removeUnknownProperties(s)
		def strings = s.tokenize(':')

		def group = (strings.size() > 2) ? strings[0] : null
		def artifactName = (strings.size() < 3) ? strings[0] : strings[1]
		def version = (strings.size() == 2) ? strings[1] : strings[2]
		version = scrubVersion(version)
	
		dependencies << new Dependency([applicationName:applicationName, group:group, name:artifactName, version:version, configuration:name])
	}
	
	private String removeUnknownProperties(String s) {
		return s.replace("[:]", "?")
	}
	
	private String scrubVersion(String version) {
		if (version?.contains('@')) {
			return version.substring(0, version.indexOf('@'))
		}
		return version
	}
	
}

@SuppressWarnings('UnusedMethodParameter')
class DoNothing extends Expando {
	
	static final INSTANCE = new DoNothing()
	
	def methodMissing(String name, args) {
		return INSTANCE
	}
	
	def propertyMissing(String name) {
		return INSTANCE
	}

}
