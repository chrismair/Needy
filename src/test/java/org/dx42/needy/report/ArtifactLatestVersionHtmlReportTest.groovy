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

import org.dx42.needy.service.ArtifactLatestVersionService
import org.dx42.needy.service.MavenCentralArtifactLatestVersionService
import org.junit.Before
import org.junit.Test

/**
 * Tests for ArtifactLatestVersionHtmlReport
 *
 * @author Chris Mair
 */
class ArtifactLatestVersionHtmlReportTest extends AbstractHtmlReportTestCase {

    private static final String TITLE = 'Latest Artifact Version'
    private static final String H1_HTML = "<h1>$TITLE</h1>"
//    private static final String METADATA_HTML = metadataHtml(TITLE)
    private static final String TABLE_HEADER_HTML = "<tr class='tableHeader'><th>#</th><th>Group</th><th>Name</th><th>Latest Version</th></tr>"

    private static final String EXPECTED_REPORT_TEXT = """
        <!DOCTYPE html><html>$HEAD_HTML    <body>$H1_HTML    $METADATA_HTML
        <div class='summary'><h2>Artifacts</h2><table>$TABLE_HEADER_HTML
        ${latestVersionRow(1, "log4j-extra", "stuff", "9.0")}
        ${latestVersionRow(2, "log4j", "log4j", "9.0")}
        ${latestVersionRow(3, "org.hibernate", "hibernate-core", "9.0")}
        ${latestVersionRow(4, "org.other", "service", "9.0")}
        </table></div>
        </body></html>
        """

    @Override
    protected String getExpectedReportText() {
        return EXPECTED_REPORT_TEXT
    }

    @Override
    protected Report createReport() {
        return new ArtifactLatestVersionHtmlReport()
    }

    protected static String latestVersionRow(int index, String group, String name, String latestVersion) {
        return "<tr><td>$index</td><td>$group</td><td>$name</td><td>$latestVersion</td></tr>"
    }

    @Before
    void before() {
        report.artifactLatestVersionService = [getLatestVersion:{ group, name -> '9.0' }] as ArtifactLatestVersionService
    }

    @Test
    void test_artifactLatestVersionService_instanceof_MavenCentralArtifactLatestVersionService() {
        def newReport = new ArtifactLatestVersionHtmlReport()
        assert newReport.artifactLatestVersionService instanceof MavenCentralArtifactLatestVersionService
    }

}
