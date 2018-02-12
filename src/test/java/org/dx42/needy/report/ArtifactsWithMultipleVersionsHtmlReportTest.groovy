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
        <!DOCTYPE html><html> $HEAD_HTML  <body>$H1_HTML  $METADATA_HTML
        <div class='summary'><h2>Artifacts with Multiple Versions</h2><table>
        $DEP_TABLE_HEADER_HTML
        ${dependencyRow(1, "log4j", "log4j", "1.2.14", "Sample1, Third")}
        ${dependencyRow(2, "log4j", "log4j", "1.2.17", "Sample_Two")}
        ${dependencyRow(3, "org.other", "service", "1.9", "Sample_Two")}
        ${dependencyRow(4, "org.other", "service", "2.0", "Third")}
        </table></div>
        $APPLICATION_NAMES_HTML
        </body></html>
        """

    private static final String EXPECTED_REPORT_TEXT_NO_LOG4J = """
        <!DOCTYPE html><html> $HEAD_HTML  <body>$H1_HTML  $METADATA_HTML
        <div class='summary'><h2>Artifacts with Multiple Versions</h2><table>
        $DEP_TABLE_HEADER_HTML
        ${dependencyRow(1, "org.other", "service", "1.9", "Sample_Two")}
        ${dependencyRow(2, "org.other", "service", "2.0", "Third")}
        </table></div>
        $APPLICATION_NAMES_HTML
        </body></html>
        """

    private static final String EXPECTED_REPORT_TEXT_NO_THIRD = """
        <!DOCTYPE html><html> $HEAD_HTML  <body>$H1_HTML  $METADATA_HTML
        <div class='summary'><h2>Artifacts with Multiple Versions</h2><table>
        $DEP_TABLE_HEADER_HTML
        ${dependencyRow(1, "log4j", "log4j", "1.2.14", "Sample1")}
        ${dependencyRow(2, "log4j", "log4j", "1.2.17", "Sample_Two")}
        </table></div>
        <h2>Application Names</h2>
        <ol>
            <li>Sample1</li>
            <li>Sample_Two</li>
        </ol>
        </body></html>
        """
        
    @Override
    protected String getExpectedReportText() {
        return EXPECTED_REPORT_TEXT
    }
    
    @Override
    protected Report createReport() {
        return new ArtifactsWithMultipleVersionsHtmlReport()
    }

    //------------------------------------------------------------------------------------
    // Tests
    //------------------------------------------------------------------------------------
    
    @Test
    void test_writeReport_EmptyDependencies_SetTitle() {
        final EXPECTED = """
            <!DOCTYPE html><html> ${headHtml("My Title")} <body>$H1_HTML
            ${metadataHtml("My Title")}
            <div class='summary'><h2>Artifacts with Multiple Versions</h2><table> $DEP_TABLE_HEADER_HTML </table></div>
            <h2>Application Names</h2><ol></ol>
            </body></html>
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
    void test_writeReport_includeArtifacts() {
        report.includeArtifacts = "org.other:service:*"
        report.writeReport(writer, DEPENDENCIES)
        assertSameXml(writer.toString(), EXPECTED_REPORT_TEXT_NO_LOG4J)
    }
    
    @Test
    void test_writeReport_excludeArtifacts() {
        report.excludeArtifacts = "log4j:*:*"
        report.writeReport(writer, DEPENDENCIES)
        assertSameXml(writer.toString(), EXPECTED_REPORT_TEXT_NO_LOG4J)
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
