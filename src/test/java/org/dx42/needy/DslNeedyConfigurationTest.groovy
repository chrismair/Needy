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
package org.dx42.needy

import org.junit.Test

import org.dx42.needy.report.ByArtifactTextReport
import org.dx42.needy.report.Report
import org.dx42.needy.report.StubReport

class DslNeedyConfigurationTest extends AbstractTestCase {

    private static final String TEST_CONFIG_FILE = "src/test/resources/test-config.txt"

    @Test
    void test_fromString_NullOrEmptyString() {
        shouldFailWithMessage("text") { DslNeedyConfiguration.fromString(null) }
        shouldFailWithMessage("text") { DslNeedyConfiguration.fromString("") }
    }

    @Test
    void test_fromFile_NullOrEmptyFilename() {
        shouldFailWithMessage("file") { DslNeedyConfiguration.fromFile(null) }
        shouldFailWithMessage("file") { DslNeedyConfiguration.fromFile("") }
    }

    @Test
    void test_fromFile_FileDoesNotExist() {
        shouldFail(IOException) { DslNeedyConfiguration.fromFile("NoSuchFile.txt") }
    }

    @Test
    void test_fromFile_ReadsInFileText() {
        def buildSet = DslNeedyConfiguration.fromFile(TEST_CONFIG_FILE)
        assert buildSet.getText() == new File(TEST_CONFIG_FILE).text
    }

    @Test
    void test_EmptyNeedyClosure() {
        def needyConfiguration = DslNeedyConfiguration.fromString("needy { }")
        assert needyConfiguration.getApplicationBuilds() == []
        assert needyConfiguration.getReports() == []
    }

    @Test
    void test_SingleApplication_NoReports() {
        final TEXT = """
            needy {
                applications {
                    Fidget(url:"http://svn/Fidget/build.gradle")
                }
            }
        """
        def needyConfiguration = DslNeedyConfiguration.fromString(TEXT)
        def applicationBuilds = needyConfiguration.getApplicationBuilds()
        assertApplicationBuild(applicationBuilds[0], "Fidget", [[url:"http://svn/Fidget/build.gradle"]])
        assert needyConfiguration.getReports() == []
    }

    @Test
    void test_getApplicationBuilds_Application_String_InvalidSyntax() {
        final TEXT = """
            needy {
                applications {
                    Wallace('blah')
                }
            }
        """
        shouldFailWithMessage("Wallace") { DslNeedyConfiguration.fromString(TEXT) }
    }

    @Test
    void test_getApplicationBuilds_Application_MapSyntax_UnknownKey() {
        final TEXT = """
            needy {
                applications {
                    Wallace([url:"http://svn/Wallace/custom-build.gradle", unknown:'123'])
                }
            }
        """
        shouldFailWithMessage("unknown") { DslNeedyConfiguration.fromString(TEXT) }
    }

    @Test
    void test_getApplicationBuilds_Application_MapSyntax_MisingUrl() {
        final TEXT = """
            needy {
                applications {
                    Wallace([description:'blah'])
                }
            }
        """
        shouldFailWithMessage("url") { DslNeedyConfiguration.fromString(TEXT) }
    }

    @Test
    void test_getApplicationBuilds_MultipleApplications() {
        final TEXT = """
            needy {
                applications {
                    Fidget(url:"http://svn/Fidget/build.gradle")
                    Wallace(url:"http://svn/Wallace/custom-build.gradle", description:"wallace", type:"gradle", componentId:"wallace", properties:[a:1])
                }
            }
        """
        def needyConfiguration = DslNeedyConfiguration.fromString(TEXT)
        def applicationBuilds = needyConfiguration.getApplicationBuilds()
        assertApplicationBuild(applicationBuilds[0], "Fidget", [[url:"http://svn/Fidget/build.gradle"]])
        assertApplicationBuild(applicationBuilds[1], "Wallace", [[url:"http://svn/Wallace/custom-build.gradle", type:"gradle", properties:[a:1]]])
        assert needyConfiguration.getReports() == []
    }

    @Test
    void test_getApplicationBuilds_MultipleUrlsPerApplication() {
        final TEXT = """
            needy {
                applications {
                    Fidget(
                        [url:"http://svn/Fidget/build.gradle", properties:[:]],
                        [url:"http://svn/Fidget2/BuildConfig.groovy", type:"grails2"])
                }
            }
        """
        def needyConfiguration = DslNeedyConfiguration.fromString(TEXT)
        def applicationBuilds = needyConfiguration.getApplicationBuilds()
        assertApplicationBuild(applicationBuilds[0], "Fidget", [[url:"http://svn/Fidget/build.gradle", properties:[:]], [url:"http://svn/Fidget2/BuildConfig.groovy", type:"grails2"]])
        assert needyConfiguration.getReports() == []
    }

    @Test
    void test_getApplicationBuilds_MultipleUrlsPerApplication_ExplicitListOfMaps() {
        final TEXT = """
            needy {
                applications {
                    Fidget([
                        [url:"http://svn/Fidget/build.gradle"],
                        [url:"http://svn/Fidget2/BuildConfig.groovy", type:"grails2"]])
                }
            }
        """
        def needyConfiguration = DslNeedyConfiguration.fromString(TEXT)
        def applicationBuilds = needyConfiguration.getApplicationBuilds()
        assertApplicationBuild(applicationBuilds[0], "Fidget", [[url:"http://svn/Fidget/build.gradle"], [url:"http://svn/Fidget2/BuildConfig.groovy", type:"grails2"]])
        assert needyConfiguration.getReports() == []
    }

    @Test
    void test_getApplicationBuilds_Application_ListSyntax_Invalid() {
        final TEXT = """
            needy {
                applications {
                    Fidget([
                        [url:"http://svn/Fidget/build.gradle"],
                        "Not a Map"])
                }
            }
        """
        shouldFailWithMessage("Each element of the List must be a Map") { DslNeedyConfiguration.fromString(TEXT) }
    }

    // Tests for Reports

    @Test
    void test_SingleReport() {
        final TEXT = """
            needy {
                applications {
                    Fidget(url:"http://svn/Fidget/build.gradle")
                }

                reports {
                    textReport("org.dx42.needy.report.ByArtifactTextReport") {
                        outputFile = "xxx"
                    }
                }
            }
        """
        def needyConfiguration = DslNeedyConfiguration.fromString(TEXT)
        def applicationBuilds = needyConfiguration.getApplicationBuilds()
        assertApplicationBuild(applicationBuilds[0], "Fidget", [[url:"http://svn/Fidget/build.gradle"]])
        assert needyConfiguration.getReports().size() == 1
        assert needyConfiguration.getReports()[0] instanceof ByArtifactTextReport
        assert needyConfiguration.getReports()[0].outputFile == "xxx"
    }

    @Test
    void test_MultipleReports() {
        final TEXT = """
            needy {
                applications {
                    Fidget(url:"http://svn/Fidget/build.gradle")
                }

                reports {
                    textReport("org.dx42.needy.report.ByArtifactTextReport") {
                        outputFile = "xxx"
                    }
                    stubReport("org.dx42.needy.report.StubReport")
                }
            }
        """
        def needyConfiguration = DslNeedyConfiguration.fromString(TEXT)
        def applicationBuilds = needyConfiguration.getApplicationBuilds()
        assertApplicationBuild(applicationBuilds[0], "Fidget", [[url:"http://svn/Fidget/build.gradle"]])
        assert needyConfiguration.getReports().size() == 2
        assert needyConfiguration.getReports()[0] instanceof ByArtifactTextReport
        assert needyConfiguration.getReports()[0].outputFile == "xxx"
        assert needyConfiguration.getReports()[1] instanceof StubReport
    }

    @Test
    void test_getApplicationBuilds_UnknownMethodOutsideApplications() {
        final TEXT = """
            needy {
                // This should not be here; should be within "applications { }"
                Fidget(["http://svn/Fidget/build.gradle", "http://svn/Fidget/build.gradle"])
            }
        """
        shouldFail(MissingMethodException) { DslNeedyConfiguration.fromString(TEXT) }
    }

    @Test
    void test_getApplicationBuilds_WrongMethodSignatureInsideReports() {
        final TEXT1 = """
            needy {
                reports {
                    unknown()
                }
            }
        """
        shouldFail(MissingMethodException) { DslNeedyConfiguration.fromString(TEXT1) }

        final TEXT2 = """
            needy {
                reports {
                    unknown("org.dx42.needy.report.StubReport", [a:1], "tooManyParameters")
                }
            }
        """
        shouldFail(MissingMethodException) { DslNeedyConfiguration.fromString(TEXT2) }
    }

    @Test
    void test_getApplicationBuilds_2ndReportParameterNotAClosure() {
        final TEXT1 = """
            needy {
                reports {
                    unknown("org.dx42.needy.report.StubReport", "shouldBeAClosure")
                }
            }
        """
        shouldFailWithMessage("must be a Closure") { DslNeedyConfiguration.fromString(TEXT1) }
    }

    @Test
    void test_getApplicationBuilds_NotAReportClass() {
        final TEXT1 = """
            needy {
                reports {
                    unknown("org.dx42.needy.Artifact")
                }
            }
        """
        shouldFailWithMessage(Report.name) { DslNeedyConfiguration.fromString(TEXT1) }
    }

    @Test
    void test_InvalidSyntaxOfNeedyConfigFile() {
        final TEXT = "%^&*()GHJ"
        shouldFail(IllegalStateException) { DslNeedyConfiguration.fromString(TEXT) }
    }

    //--------------------------------------------------------------------------
    // Helper methods
    //--------------------------------------------------------------------------

    private void assertApplicationBuild(ApplicationBuild actual, String name, List<Map> expectedBuildScripts) {
        assert actual.name == name
        assert actual.buildScripts.size() == expectedBuildScripts.size()
        assert actual.buildScripts.every { buildScript -> buildScript instanceof UrlBuildScript }
        expectedBuildScripts.eachWithIndex { Map expectedBuildScript, int index ->
            assert actual.buildScripts[index].url.toString() == expectedBuildScript.url
            assert actual.buildScripts[index].type == expectedBuildScript.type
            assert actual.buildScripts[index].properties == expectedBuildScript.properties
        }
    }

}
