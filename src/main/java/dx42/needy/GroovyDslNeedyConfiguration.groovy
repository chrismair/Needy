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
package dx42.needy

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.codehaus.groovy.control.MultipleCompilationErrorsException

class GroovyDslNeedyConfiguration implements NeedyConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(GroovyDslNeedyConfiguration)
	
	private String text
	private List<ApplicationBuild> applicationBuilds
	private List<ReportWriter> reportWriters
	
	static GroovyDslNeedyConfiguration fromFile(String file) {
		assert file, "The file parameter must not be null or empty"
		String text = new File(file).text
		return new GroovyDslNeedyConfiguration(text)
	}
	
	static GroovyDslNeedyConfiguration fromString(String text) {
		assert text, "The text parameter must not be null or empty"
		return new GroovyDslNeedyConfiguration(text)
	}
	
	private GroovyDslNeedyConfiguration(String text) {
		this.text = text
		parse(text)
	}
	
	@Override
	List<ApplicationBuild> getApplicationBuilds() {
		return applicationBuilds
	}
	
	@Override
	List<ReportWriter> getReportWriters() {
		return reportWriters
	}
	
	// For testing
	protected String getText() {
		return text
	}

	private void parse(String source) {
		assert source

		DslEvaluator dslEvaluator = new DslEvaluator()
		
		GroovyShell shell = createGroovyShell(dslEvaluator)
		try {
			shell.evaluate(source)
		}
		catch (MultipleCompilationErrorsException compileError) {
			LOG.error("An error occurred compiling: [$source]", compileError)
			throw new IllegalStateException("An error occurred compiling: [$source]\n${compileError.message}")
		}

		applicationBuilds = dslEvaluator.applicationBuilds
		reportWriters = dslEvaluator.reportWriters
		LOG.info "applicationBuilds = $applicationBuilds"
	}

	private GroovyShell createGroovyShell(DslEvaluator dslEvaluator) {
		def callNeedy = { Closure closure ->
			dslEvaluator.evaluate(closure)
		}
		Map bindingMap = [needy:callNeedy]
		Binding binding = new Binding(bindingMap)

		return new GroovyShell(this.class.classLoader, binding)
	}

}

class DslEvaluator {
	
	private static final Logger LOG = LoggerFactory.getLogger(DslEvaluator)

	List<ApplicationBuild> applicationBuilds = []
	List<ReportWriter> reportWriters = []
	private boolean withinApplications = false
	private boolean withinReports = false
	
	void evaluate(Closure closure) {
		closure.delegate = this
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure.call()
	}
	
	def applications(Closure closure) {
		withinApplications = true
		closure.call()
		withinApplications = false
	}
	
	def reports(Closure closure) {
		withinReports = true
		closure.call()
		withinReports = false
	}
	
	def report(String reportClassName, Closure closure) {
		Class reportClass = Class.forName(reportClassName)
		ReportWriter reportWriter = reportClass.newInstance() 
		reportWriters << reportWriter
		
		closure.delegate = reportWriter
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()
	}
	
	def report(String reportClassName) {
		Class reportClass = Class.forName(reportClassName)
		ReportWriter reportWriter = reportClass.newInstance() 
		reportWriters << reportWriter
	}
	
	def methodMissing(String name, def args) {
		if (!withinApplications) {
			throw new MissingMethodException(name, getClass(), args)
		}
		if (args[0] instanceof String) {
			LOG.info "methodMissing (String): name=$name value=${args[0]}"
			applicationBuilds << new ApplicationBuild(name, [new UrlBuildScript(args[0])])
		}
		else if (args[0] instanceof List) {
			List list = args[0]
			LOG.info "methodMissing (List): name=$name value=${list}"
			def urlBuildScripts = list.collect { String url -> 
				new UrlBuildScript(url)
			}
			applicationBuilds << new ApplicationBuild(name, urlBuildScripts)
		}
		else throw new MissingMethodException(name, getClass(), args)
	}
}
