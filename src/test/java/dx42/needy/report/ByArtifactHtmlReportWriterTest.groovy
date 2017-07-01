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
package dx42.needy.report

import org.junit.Test

import dx42.needy.AbstractTestCase
import dx42.needy.Dependency
import org.junit.Before

class ByArtifactHtmlReportWriterTest extends AbstractTestCase {

	private static final String TEXT = "abc123"
	private static final String OUTPUT_FILE = "src/test/resources/temp-html-report.html"
	private static final List<Dependency> DEPENDENCIES = [
		new Dependency(applicationName:"Third", group:"org.other", name:"service", version:"2.0"),
		new Dependency(applicationName:"Sample1", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
		new Dependency(applicationName:"Sample_Two", group:"log4j", name:"log4j", version:"1.2.14"),
		new Dependency(applicationName:"Sample1", group:"log4j", name:"log4j", version:"1.2.14"),
		new Dependency(applicationName:"Third", group:"log4j", name:"log4j", version:"1.2.14"),
		new Dependency(applicationName:"Third", group:"log4j-extra", name:"stuff", version:"1.0"),
	]
	private static final String TIMESTAMP_STRING = "Jul 1, 2017 7:20:09 AM"
	private static final String CSS_FILE = "src/main/resources/htmlreport.css"
	private static final String CSS = new File(CSS_FILE).text
	

	private static final String EXPECTED_REPORT_TEXT = """
		<html><head><title>Needy Report: Dependency Report</title><style type='text/css'>$CSS</style></head>
		<body><h1>Dependency Report</h1><div class='metadata'><table><tr><td class='em'>Report Title:</td><td>Dependency Report</td></tr><tr><td class='em'>Timestamp:</td><td>Jul 1, 2017 7:20:09 AM</td></tr><tr><td class='em'>Generated With:</td><td><a href='https://github.com/dx42/Needy'>Needy</a></td></tr></table></div><div class='summary'><h2>Dependencies</h2><table><tr class='tableHeader'><th>Group</th><th>Name</th><th>Version</th><th>Applications</th></tr><tr><td>log4j</td><td>log4j</td><td>1.2.14</td><td class='applicationNames'>Sample1, Sample_Two, Third</td></tr><tr><td>log4j-extra</td><td>stuff</td><td>1.0</td><td class='applicationNames'>Third</td></tr><tr><td>org.hibernate</td><td>hibernate-core</td><td>3.1</td><td class='applicationNames'>Sample1</td></tr><tr><td>org.other</td><td>service</td><td>2.0</td><td class='applicationNames'>Third</td></tr></table></div></body></html>
		"""

	private ByArtifactHtmlReportWriter reportWriter = new ByArtifactHtmlReportWriter()
	private StringWriter writer = new StringWriter()
	
	@Test
	void test_writeReport_Null() {
		shouldFailWithMessage("dependencies") { reportWriter.writeReport(null) }
	}

	@Test
	void test_writeReport_WritesToStdOut() {
		def output = captureSystemOut {
			reportWriter.writeReport(DEPENDENCIES)
		}
		assertSameXml(output, EXPECTED_REPORT_TEXT)
	}

	@Test
	void test_writeReport_EmptyDependencies() {
		final EXPECTED = """
			<html><head><title>Needy Report: Dependency Report</title><style type='text/css'>$CSS</style></head>
			<body><h1>Dependency Report</h1><div class='metadata'><table><tr><td class='em'>Report Title:</td><td>Dependency Report</td></tr><tr><td class='em'>Timestamp:</td><td>Jul 1, 2017 7:20:09 AM</td></tr><tr><td class='em'>Generated With:</td><td><a href='https://github.com/dx42/Needy'>Needy</a></td></tr></table></div><div class='summary'><h2>Dependencies</h2><table><tr class='tableHeader'><th>Group</th><th>Name</th><th>Version</th><th>Applications</th></tr></table></div></body></html>
			"""
		reportWriter.writeReport(writer, [])
		assertSameXml(writer.toString(), EXPECTED)
	}
	
	@Test
	void test_writeReport_Dependencies() {
		reportWriter.writeReport(writer, DEPENDENCIES)
		assertSameXml(writer.toString(), EXPECTED_REPORT_TEXT)
	}
	
	@Test
	void test_writeReport_OutputFile_WritesToFile() {
		reportWriter.outputFile = OUTPUT_FILE
		reportWriter.writeReport(DEPENDENCIES)

		def file = new File(OUTPUT_FILE)
		file.deleteOnExit()
		
		assertSameXml(file.text, EXPECTED_REPORT_TEXT)
	}
	
	@Test
	void test_writeReport_OutputFile_CannotCreateOutputFile() {
		reportWriter.outputFile = "noSuchDir/orSubDir/file.txt"
		shouldFail(FileNotFoundException) { reportWriter.writeReport(DEPENDENCIES) }
	}

	@Before
	void setUp() {
		reportWriter.getFormattedTimestamp = { TIMESTAMP_STRING }
	}
		
}
