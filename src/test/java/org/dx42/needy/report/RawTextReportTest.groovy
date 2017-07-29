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
package org.dx42.needy.report

import org.junit.Test

import org.dx42.needy.AbstractTestCase
import org.dx42.needy.Dependency

class RawTextReportTest extends AbstractTestCase {

	private RawTextReport report = new RawTextReport()
	
	@Test
	void test_writeReport_Null() {
		shouldFailWithMessage("dependencies") { report.writeReport(null) }
	}
	
	@Test
	void test_writerReport() {
		def dependencies = [
			new Dependency(applicationName:"Sample1", configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(applicationName:"Sample1", configuration:"compile", group:"log4j", name:"log4j", version:"1.2.14"),
			new Dependency(applicationName:"Sample_Two", configuration:"compile", group:"log4j", name:"log4j", version:"1.2.14"),
		]
		
		def output = captureSystemOut {
			report.writeReport(dependencies)
		}

		log "output=\n$output"

		assert output.trim() == """
Dependency(applicationName:Sample1, group:org.hibernate, name:hibernate-core, version:3.1, configuration:compile)
Dependency(applicationName:Sample1, group:log4j, name:log4j, version:1.2.14, configuration:compile)
Dependency(applicationName:Sample_Two, group:log4j, name:log4j, version:1.2.14, configuration:compile)
		""".trim()
	}
	
}
