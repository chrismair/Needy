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

class ArtifactsWithMultipleVersionsHtmlReportTest extends AbstractHtmlReportTestCase {

	private static final String EXPECTED_REPORT_TEXT = """
		<!DOCTYPE html><html><head><title>Needy Dependency Report: Dependency Report</title><meta http-equiv="Content-Type" content="text/html"><style type='text/css'>$CSS</style></head>
		<body><h1>Needy Dependency Report</h1><div class='metadata'><table><tr><td class='em'>Report Title:</td><td class='reportTitle'>Dependency Report</td></tr>
		<tr><td class='em'>Timestamp:</td><td>$TIMESTAMP_STRING</td></tr>
		<tr><td class='em'>Generated With:</td><td><a href='https://github.com/dx42/Needy'>Needy v$VERSION</a></td></tr></table></div>
		<div class='summary'><h2>Artifacts with Multiple Versions</h2><table><tr class='tableHeader'><th>#</th><th>Group</th><th>Name</th><th>Version</th><th>Applications</th></tr>
		<tr><td>1</td><td>log4j</td><td>log4j</td><td>1.2.14</td><td class='applicationNames'>Sample1, Third</td></tr>
		<tr><td>2</td><td>log4j</td><td>log4j</td><td>1.2.17</td><td class='applicationNames'>Sample_Two</td></tr>
		<tr><td>3</td><td>org.other</td><td>service</td><td>1.9</td><td class='applicationNames'>Sample_Two</td></tr>
		<tr><td>4</td><td>org.other</td><td>service</td><td>2.0</td><td class='applicationNames'>Third</td></tr>
		</table></div></body></html>
		"""

	private static final String EXPECTED_REPORT_TEXT_NO_THIRD = """
		<!DOCTYPE html><html><head><title>Needy Dependency Report: Dependency Report</title><meta http-equiv="Content-Type" content="text/html"><style type='text/css'>$CSS</style></head>
		<body><h1>Needy Dependency Report</h1><div class='metadata'><table><tr><td class='em'>Report Title:</td><td class='reportTitle'>Dependency Report</td></tr>
		<tr><td class='em'>Timestamp:</td><td>$TIMESTAMP_STRING</td></tr>
		<tr><td class='em'>Generated With:</td><td><a href='https://github.com/dx42/Needy'>Needy v$VERSION</a></td></tr></table>
		</div><div class='summary'><h2>Artifacts with Multiple Versions</h2><table><tr class='tableHeader'><th>#</th>
		<th>Group</th><th>Name</th><th>Version</th><th>Applications</th></tr>
		<tr><td>1</td><td>log4j</td><td>log4j</td><td>1.2.14</td><td class='applicationNames'>Sample1</td></tr>
		<tr><td>2</td><td>log4j</td><td>log4j</td><td>1.2.17</td><td class='applicationNames'>Sample_Two</td></tr>
		</table></div></body></html>
		"""
		
    protected String getExpectedReportText() {
        return EXPECTED_REPORT_TEXT
    }
    
    protected Report createReport() {
        return new ArtifactsWithMultipleVersionsHtmlReport()
    }

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------
    
	@Test
	void test_writeReport_EmptyDependencies_SetTitle() {
		final EXPECTED = """
			<!DOCTYPE html><html><head><title>Needy Dependency Report: My Title</title><meta http-equiv="Content-Type" content="text/html"><style type='text/css'>$CSS</style></head>
			<body><h1>Needy Dependency Report</h1><div class='metadata'><table><tr><td class='em'>Report Title:</td><td class='reportTitle'>My Title</td></tr><tr><td class='em'>Timestamp:</td><td>$TIMESTAMP_STRING</td></tr><tr><td class='em'>Generated With:</td><td><a href='https://github.com/dx42/Needy'>Needy v$VERSION</a></td></tr></table></div><div class='summary'>
			<h2>Artifacts with Multiple Versions</h2><table><tr class='tableHeader'><th>#</th><th>Group</th><th>Name</th><th>Version</th><th>Applications</th></tr></table></div></body></html>
			"""
		report.title = "My Title"
		report.writeReport(writer, [])
		assertSameXml(writer.toString(), EXPECTED)
	}
	
	@Test
	void test_writeReport_Dependencies() {
		report.writeReport(writer, DEPENDENCIES)
		assertSameXml(writer.toString(), EXPECTED_REPORT_TEXT)
	}
	
	@Test
	void test_writeReport_includeApplications() {
		report.includeApplications = "Sample*, Other"
		report.writeReport(writer, DEPENDENCIES)
		assertSameXml(writer.toString(), EXPECTED_REPORT_TEXT_NO_THIRD)
	}
	
	@Test
	void test_writeReport_excludeApplications() {
		report.excludeApplications = "Th*"
		report.writeReport(writer, DEPENDENCIES)
		assertSameXml(writer.toString(), EXPECTED_REPORT_TEXT_NO_THIRD)
	}
	
	@Test
	void test_writeReport_notesHtml() {
		String notesHtml = "<h2>Notes</h2><p>Some text</p>"
		report.notesHtml = notesHtml
		report.writeReport(writer, DEPENDENCIES)
		String expected = EXPECTED_REPORT_TEXT.replace("<div class='summary'>", notesHtml + "<div class='summary'>")
		assertSameXml(writer.toString(), expected)
	}
	
}
