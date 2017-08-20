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

import org.dx42.needy.AbstractTestCase
import org.dx42.needy.Dependency
import org.dx42.needy.NeedyVersion
import org.junit.Before
import org.junit.Test

abstract class AbstractHtmlReportTestCase extends AbstractTestCase {

    protected static final String OUTPUT_FILE = "src/test/resources/temp-html-report.html"
    protected static final List<Dependency> DEPENDENCIES = [
        new Dependency(applicationName:"Sample1", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
        new Dependency(applicationName:"Sample1", group:"log4j", name:"log4j", version:"1.2.14"),
        new Dependency(applicationName:"Sample_Two", group:"log4j", name:"log4j", version:"1.2.17"),
        new Dependency(applicationName:"Sample_Two", group:"org.other", name:"service", version:"1.9"),
        new Dependency(applicationName:"Third", group:"org.other", name:"service", version:"2.0"),
        new Dependency(applicationName:"Third", group:"log4j", name:"log4j", version:"1.2.14"),
        new Dependency(applicationName:"Third", group:"log4j-extra", name:"stuff", version:"1.0"),
    ]
    protected static final String TIMESTAMP_STRING = "Jul 1, 2017 7:20:09 AM"
    protected static final String CSS_FILE = "src/main/resources/htmlreport.css"
    protected static final String CSS = new File(CSS_FILE).text
    protected static final String VERSION = NeedyVersion.version

    protected static final String HEAD_HTML = """<head><title>Needy Dependency Report: Dependency Report</title><meta http-equiv="Content-Type" content="text/html"><style type='text/css'>$CSS</style></head>"""
    protected static final String H1_HTML = "<h1>Needy Dependency Report</h1>"
    protected static final String METADATA_HTML = metadataHtml("Dependency Report")
    protected static final String DEP_TABLE_HEADER_HTML = "<tr class='tableHeader'><th>#</th><th>Group</th><th>Name</th><th>Version</th><th>Applications</th></tr>"
    protected static final String APPLICATION_NAMES_HTML = """
        <h2>Application Names</h2>
        <ol>
            <li>Sample1</li>
            <li>Sample_Two</li>
            <li>Third</li>
        </ol>
    """

    protected static String metadataHtml(String title) {
        return """
        <div class='metadata'><table><tr><td class='em'>Report Title:</td><td class='reportTitle'>$title</td></tr>
        <tr><td class='em'>Timestamp:</td><td>$TIMESTAMP_STRING</td></tr>
        <tr><td class='em'>Generated With:</td><td><a href='https://github.com/dx42/Needy'>Needy v$VERSION</a></td></tr></table></div>"""
    }
    protected static String dependencyRow(int index, String group, String name, String version, String applicationNames) {
        return "<tr><td>$index</td><td>$group</td><td>$name</td><td>$version</td><td class='applicationNames'>$applicationNames</td></tr>"
    }
    protected static String headHtml(String title) {
        return """<head><title>Needy Dependency Report: $title</title><meta http-equiv="Content-Type" content="text/html"><style type='text/css'>$CSS</style></head>"""
    }

    protected Report report
    protected StringWriter writer = new StringWriter()
    
    protected abstract String getExpectedReportText()
    protected abstract Report createReport()
    
    //------------------------------------------------------------------------------------
    // Common Tests
    //------------------------------------------------------------------------------------
    
    @Test
    void test_writeReport_Null() {
        shouldFailWithMessage("dependencies") { report.writeReport(null) }
    }

    @Test
    void test_writeReport_WritesToStdOut() {
        def output = captureSystemOut {
            report.writeReport(DEPENDENCIES)
        }
        assertSameXml(output, getExpectedReportText())
    }

    @Test
    void test_writeReport_OutputFile_WritesToFile() {
        report.outputFile = OUTPUT_FILE
        report.writeReport(DEPENDENCIES)

        def file = new File(OUTPUT_FILE)
        file.deleteOnExit()
        
        assertSameXml(file.text, getExpectedReportText())
    }
    
    @Test
    void test_writeReport_OutputFile_CannotCreateOutputFile() {
        report.outputFile = "///noSuchDir/orSubDir/file.txt"
        shouldFail(FileNotFoundException) { report.writeReport(DEPENDENCIES) }
    }

    //------------------------------------------------------------------------------------
    // Setup and Helper Methods
    //------------------------------------------------------------------------------------
    
    @Before
    void setUp_AbstractHtmlReportTestCase() {
        report = createReport()
        report.getFormattedTimestamp = { TIMESTAMP_STRING }
    }

}
