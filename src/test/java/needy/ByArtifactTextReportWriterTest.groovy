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

class ByArtifactTextReportWriterTest extends AbstractTestCase {

	private static final String TEXT = "abc123"
	
	private ByArtifactTextReportWriter reportWriter = new ByArtifactTextReportWriter()
	
	@Test
	void test_writeReport_Null() {
		shouldFailWithMessage("dependencies") { reportWriter.writeReport(null) }
	}
	
	@Test
	void test_writerReport() {
		def dependencies = [
            new Dependency(applicationName:"Third", group:"org.other", name:"service", version:"2.0"),
			new Dependency(applicationName:"Sample1", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(applicationName:"Sample_Two", group:"log4j", name:"log4j", version:"1.2.14"),
			new Dependency(applicationName:"Sample1", group:"log4j", name:"log4j", version:"1.2.14"),
			new Dependency(applicationName:"Third", group:"log4j", name:"log4j", version:"1.2.14"),
			new Dependency(applicationName:"Third", group:"log4j-extra", name:"stuff", version:"1.0"),
		]
		
		def output = captureSystemOut {
			reportWriter.writeReport(dependencies)
		}
		log "output=\n$output"

		assert output.trim() == """
Needy

"log4j:log4j:1.2.14" -- [Sample1, Sample_Two, Third]
"log4j-extra:stuff:1.0" -- [Third]
"org.hibernate:hibernate-core:3.1" -- [Sample1]
"org.other:service:2.0" -- [Third]
		""".trim()
	}
	
}
