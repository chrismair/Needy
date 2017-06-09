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
package needy

import org.junit.Test

class NeedyRunnerTest extends AbstractTestCase {

	private static final String CONFIG_TEXT = """
		needy {
			applications {
				Sample1("file:src/test/resources/sample1-build.gradle")
				Sample_Two("file:src/test/resources/sample2-build.gradle")
			}
		}"""

	private static final DEPENDENCIES = [
		new Dependency(applicationName:"Sample1", configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
		new Dependency(applicationName:"Sample1", configuration:"compile", group:"log4j", name:"log4j", version:"1.2.14"),
		new Dependency(applicationName:"Sample1", configuration:"compile", group:"org.gmetrics", name:"GMetrics", version:"0.7"),
		new Dependency(applicationName:"Sample1", configuration:"testCompile", group:"junit", name:"junit", version:"4.8.1"),
		new Dependency(applicationName:"Sample1", configuration:"testCompile", group:"commons-cli", name:"commons-cli", version:"1.2"),
		
		new Dependency(applicationName:"Sample_Two", configuration:"compile", group:"log4j", name:"log4j", version:"1.2.14"),
		new Dependency(applicationName:"Sample_Two", configuration:"compile", group:"org.codenarc", name:"CodeNarc", version:"0.28"),
		new Dependency(applicationName:"Sample_Two", configuration:"testCompile", group:"junit", name:"junit", version:"4.12") ]

	private NeedyRunner needyRunner = new NeedyRunner()
	
	@Test
	void test_execute_RequiredPropertiesNotInitialized() {
		shouldFailWithMessage("applicationBuildSet") { needyRunner.execute() }
	}
	
	@Test
	void test_execute_NoReportWriters() {
		def applicationBuildSet = GroovyDslApplicationBuildSet.fromString(CONFIG_TEXT)
		def applicationBuilds = applicationBuildSet.getApplicationBuilds()
		log(applicationBuilds)
		needyRunner.applicationBuildSet = applicationBuildSet
		def result = needyRunner.execute()
		
		assert result == DEPENDENCIES
	}
	
	@Test
	void test_execute_ReportWriters() {
		def applicationBuildSet = GroovyDslApplicationBuildSet.fromString(CONFIG_TEXT)
		needyRunner.applicationBuildSet = applicationBuildSet
		def called = [:]
		def reportWriter1 = [writeReport:{ dependencies -> 
			assert dependencies == DEPENDENCIES
			called.reportWriter1 = true }] as ReportWriter
		def reportWriter2 = [writeReport:{ dependencies -> 
			assert dependencies == DEPENDENCIES
			called.reportWriter2 = true }] as ReportWriter
		
		needyRunner.reportWriters = [reportWriter1, reportWriter2]
		def result = needyRunner.execute()
		
		assert result == DEPENDENCIES
		assert called.reportWriter1
		assert called.reportWriter2
	}
	
}
