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
import org.dx42.needy.Artifact
import org.dx42.needy.Dependency
import org.junit.Test

/**
 * Tests for AbstractReport
 *
 * @author Chris Mair
 */
class AbstractReportTest extends AbstractTestCase {

    private static final Date DATE = new Date(1499477419708L)
    private static final List<Dependency> DEPENDENCIES = [
        new Dependency(applicationName:"Third", group:"org.other", name:"service", version:"2.0"),
        new Dependency(applicationName:"Sample1", group:"ORG.hibernate", name:"hibernate-core", version:"3.1"),
        new Dependency(applicationName:"sample_Two", group:"log4j", name:"log4j", version:"1.2.14"),
        new Dependency(applicationName:"Sample1", group:"log4j", name:"log4j", version:"1.2.14"),
        new Dependency(applicationName:"Third", group:"log4j", name:"log4j", version:"1.2.14"),
        new Dependency(applicationName:"Third", group:"log4j-extra", name:"stuff", version:"1.0"),
    ]
    private Writer writer
    private List<Dependency> dependencies
    private AbstractReport report = new AbstractReport() {

        @Override
        void writeReport(Writer writer, List<Dependency> dependencies) {
            this.writer = writer
            this.dependencies = dependencies
        }
    }

    //--------------------------------------------------------------------------
    // Tests
    //--------------------------------------------------------------------------

    @Test
    void test_writeReport() {
        report.writeReport(DEPENDENCIES)
        assert dependencies == DEPENDENCIES
        assert writer instanceof PrintWriter
    }

    @Test
    void test_writeReport_CreateParentDirectoriesIfNecessary() {
        def parentDir = "src/test/resources/doesNotExist"
        def reportFile = parentDir + "/tmpfile.txt"
        report.outputFile = reportFile
        new File(parentDir).deleteOnExit()
        new File(reportFile).deleteOnExit()
        report.writeReport(DEPENDENCIES)
        assert dependencies == DEPENDENCIES
    }

    @Test
    void test_writeReport_NullDependencies() {
        shouldFailWithMessage('dependencies') { report.writeReport(null) }
    }

    @Test
    void test_getFormattedTimestamp() {
        report.getDate = { DATE }
        log(report.getFormattedTimestamp())
        assert report.getFormattedTimestamp() == java.text.DateFormat.getDateTimeInstance().format(DATE)
    }

    @Test
    void test_createPrintWriter() {
        def writer = report.createPrintWriter()
        assert writer
    }

    @Test
    void test_createPrintWriter_outputFile() {
        report.outputFile = "build/tmpfile.txt"
        new File(report.outputFile).deleteOnExit()
        def writer = report.createPrintWriter()
        assert writer
    }

    @Test
    void test_getDate() {
        assert report.getDate() instanceof Date
    }

    @Test
    void test_getApplicationNames() {
        assert report.getApplicationNames(DEPENDENCIES) == ["Sample1", "sample_Two", "Third"] as Set

        report.excludeApplications = "*1"
        assert report.getApplicationNames(DEPENDENCIES) == ["sample_Two", "Third"] as Set

        report.includeApplications = "*amp*"
        assert report.getApplicationNames(DEPENDENCIES) == ["sample_Two"] as Set
    }

    // Tests for include/exclude applications

    @Test
    void test_matchesIncludeApplication() {
        assert report.matchesIncludeApplications("a")

        report.includeApplications = "A*"
        assert report.matchesIncludeApplications("AAA")
        assert report.matchesIncludeApplications("A-B")
        assert !report.matchesIncludeApplications("B")

        report.includeApplications = "A, B*"
        assert report.matchesIncludeApplications("A")
        assert report.matchesIncludeApplications("B")
        assert report.matchesIncludeApplications("BAB")
        assert !report.matchesIncludeApplications("A-B")
        assert !report.matchesIncludeApplications("AB")
    }

    @Test
    void test_matchesExcludeApplications() {
        assert !report.matchesExcludeApplications("a")

        report.excludeApplications = "A*"
        assert report.matchesExcludeApplications("AAA")
        assert report.matchesExcludeApplications("A-B")
        assert !report.matchesExcludeApplications("B")

        report.excludeApplications = "A, B*"
        assert report.matchesExcludeApplications("A")
        assert report.matchesExcludeApplications("B")
        assert report.matchesExcludeApplications("BAB")
        assert !report.matchesExcludeApplications("A-B")
        assert !report.matchesExcludeApplications("AB")
    }

    @Test
    void test_isIncludedApplication() {
        report.includeApplications = "A*"
        report.excludeApplications = "B*, CCC"

        assert report.isIncludedApplication("AAA")
        assert report.isIncludedApplication("ABC")

        assert !report.isIncludedApplication("B")
        assert !report.isIncludedApplication("BBB")
        assert !report.isIncludedApplication("CCC")
        assert !report.isIncludedApplication("CXX")
        assert !report.isIncludedApplication("Other")
    }

    // Tests for include/exclude artifacts

    private static final Artifact ARTIFACT_A_X_1 = new Artifact(group:'A', name:'X', version:'1')
    private static final Artifact ARTIFACT_A_X_2 = new Artifact(group:'A', name:'X', version:'2')
    private static final Artifact ARTIFACT_A_Y_1 = new Artifact(group:'A', name:'Y', version:'1')
    private static final Artifact ARTIFACT_AA_XX_11 = new Artifact(group:'AA', name:'XX', version:'11')
    private static final Artifact ARTIFACT_BB_XX_22 = new Artifact(group:'BB', name:'XX', version:'22')
    private static final Artifact ARTIFACT_BB_YY_11 = new Artifact(group:'BB', name:'YY', version:'11')

    @Test
    void test_matchesIncludeArtifacts() {
        assert report.matchesIncludeArtifacts(ARTIFACT_A_X_1)

        report.includeArtifacts = "A:X*:*1"
        assert report.matchesIncludeArtifacts(ARTIFACT_A_X_1)
        assert !report.matchesIncludeArtifacts(ARTIFACT_A_X_2)
        assert !report.matchesIncludeArtifacts(ARTIFACT_A_Y_1)

        report.includeArtifacts = "BB:YY:11, A*:X*:*"
        assert report.matchesIncludeArtifacts(ARTIFACT_A_X_1)
        assert report.matchesIncludeArtifacts(ARTIFACT_A_X_2)
        assert report.matchesIncludeArtifacts(ARTIFACT_AA_XX_11)
        assert report.matchesIncludeArtifacts(ARTIFACT_BB_YY_11)

        assert !report.matchesIncludeArtifacts(ARTIFACT_BB_XX_22)
        assert !report.matchesIncludeArtifacts(ARTIFACT_A_Y_1)
    }

    @Test
    void test_matchesExcludeArtifacts() {
        assert !report.matchesExcludeArtifacts(ARTIFACT_A_X_1)

        report.excludeArtifacts = "A:X*:*2"
        assert report.matchesExcludeArtifacts(ARTIFACT_A_X_2)
        assert !report.matchesExcludeArtifacts(ARTIFACT_A_X_1)
        assert !report.matchesExcludeArtifacts(ARTIFACT_AA_XX_11)

        report.excludeArtifacts = "BB:YY:11, A*:X*:*"
        assert report.matchesExcludeArtifacts(ARTIFACT_A_X_1)
        assert report.matchesExcludeArtifacts(ARTIFACT_A_X_2)
        assert report.matchesExcludeArtifacts(ARTIFACT_AA_XX_11)
        assert report.matchesExcludeArtifacts(ARTIFACT_BB_YY_11)
        assert !report.matchesExcludeArtifacts(ARTIFACT_A_Y_1)
        assert !report.matchesExcludeArtifacts(ARTIFACT_BB_XX_22)
    }

    @Test
    void test_isIncludedArtifact() {
        report.includeArtifacts = "A*"
        report.excludeArtifacts = "B*:*:*, *:*:2"

        assert report.isIncludedArtifact(ARTIFACT_A_X_1)
        assert !report.isIncludedArtifact(ARTIFACT_A_X_2)
        assert report.isIncludedArtifact(ARTIFACT_AA_XX_11)
        assert !report.isIncludedArtifact(ARTIFACT_BB_YY_11)
        assert report.isIncludedArtifact(ARTIFACT_A_Y_1)
        assert !report.isIncludedArtifact(ARTIFACT_BB_XX_22)
    }

}
