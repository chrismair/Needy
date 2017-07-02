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
package dx42.needy

import org.junit.Test

import dx42.needy.report.ByArtifactTextReportWriter
import dx42.needy.report.StubReportWriter

class GroovyDslNeedyConfigurationTest extends AbstractTestCase {

	private static final TEST_CONFIG_FILE = "src/test/resources/test-config.txt"
	
	@Test
	void test_fromString_NullOrEmptyString() {
		shouldFailWithMessage("text") { GroovyDslNeedyConfiguration.fromString(null) }
		shouldFailWithMessage("text") { GroovyDslNeedyConfiguration.fromString("") }
	}

	@Test
	void test_fromFile_NullOrEmptyFilename() {
		shouldFailWithMessage("file") { GroovyDslNeedyConfiguration.fromFile(null) }
		shouldFailWithMessage("file") { GroovyDslNeedyConfiguration.fromFile("") }
	}

	@Test
	void test_fromFile_FileDoesNotExist() {
		shouldFail(IOException) { GroovyDslNeedyConfiguration.fromFile("NoSuchFile.txt") }
	}

	@Test
	void test_fromFile_ReadsInFileText() {
		def buildSet = GroovyDslNeedyConfiguration.fromFile(TEST_CONFIG_FILE)
		assert buildSet.getText() == new File(TEST_CONFIG_FILE).text
	}

	@Test
	void test_EmptyNeedyClosure() {
		def needyConfiguration = GroovyDslNeedyConfiguration.fromString("needy { }")
		assert needyConfiguration.getApplicationBuilds() == []
		assert needyConfiguration.getReportWriters() == []
	}

	@Test
	void test_SingleApplication_NoReports() {
		final TEXT = """
			needy {
				applications {
					Fidget("http://svn/Fidget/build.gradle")
				}
			}
		"""
		def needyConfiguration = GroovyDslNeedyConfiguration.fromString(TEXT)
		assertApplicationBuilds(needyConfiguration.getApplicationBuilds(), [[name:"Fidget", urls:["http://svn/Fidget/build.gradle"]]]) 
		assert needyConfiguration.getReportWriters() == []
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
		shouldFailWithMessage("unknown") { GroovyDslNeedyConfiguration.fromString(TEXT) }
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
		shouldFailWithMessage("url") { GroovyDslNeedyConfiguration.fromString(TEXT) }
	}
	
	@Test
	void test_getApplicationBuilds_MultipleApplications() {
		final TEXT = """
			needy {
				applications {
					Fidget("http://svn/Fidget/build.gradle")
					Wallace([url:"http://svn/Wallace/custom-build.gradle", description:"wallace"])
				}
			}
		"""
		def needyConfiguration = GroovyDslNeedyConfiguration.fromString(TEXT)
		assertApplicationBuilds(needyConfiguration.getApplicationBuilds(), [
			[name:"Fidget", urls:["http://svn/Fidget/build.gradle"]], 
			[name:"Wallace", urls:["http://svn/Wallace/custom-build.gradle"]]]) 
		assert needyConfiguration.getReportWriters() == []
	}
	
	@Test
	void test_getApplicationBuilds_MultipleUrlsPerApplication() {
		final TEXT = """
			needy {
				applications {
					Fidget(["http://svn/Fidget/build.gradle", "http://svn/Fidget2/build2.gradle"])
				}
			}
		"""
		def needyConfiguration = GroovyDslNeedyConfiguration.fromString(TEXT)
		assertApplicationBuilds(needyConfiguration.getApplicationBuilds(), [
			[name:"Fidget", urls:["http://svn/Fidget/build.gradle", "http://svn/Fidget2/build2.gradle"]]]) 
		assert needyConfiguration.getReportWriters() == []
	}
	
	@Test
	void test_SingleReport() {
		final TEXT = """
			needy {
				applications {
					Fidget("http://svn/Fidget/build.gradle")
				}

				reports {
					report("dx42.needy.report.ByArtifactTextReportWriter") {
						outputFile = "xxx"
					}
				}
			}
		"""
		def needyConfiguration = GroovyDslNeedyConfiguration.fromString(TEXT)
		assertApplicationBuilds(needyConfiguration.getApplicationBuilds(), [[name:"Fidget", urls:["http://svn/Fidget/build.gradle"]]]) 
		assert needyConfiguration.getReportWriters().size() == 1
		assert needyConfiguration.getReportWriters()[0] instanceof ByArtifactTextReportWriter
		assert needyConfiguration.getReportWriters()[0].outputFile == "xxx"
	}
	
	@Test
	void test_MultipleReports() {
		final TEXT = """
			needy {
				applications {
					Fidget("http://svn/Fidget/build.gradle")
				}

				reports {
					report("dx42.needy.report.ByArtifactTextReportWriter") {
						outputFile = "xxx"
					}
					report("dx42.needy.report.StubReportWriter")
				}
			}
		"""
		def needyConfiguration = GroovyDslNeedyConfiguration.fromString(TEXT)
		assertApplicationBuilds(needyConfiguration.getApplicationBuilds(), [[name:"Fidget", urls:["http://svn/Fidget/build.gradle"]]]) 
		assert needyConfiguration.getReportWriters().size() == 2
		assert needyConfiguration.getReportWriters()[0] instanceof ByArtifactTextReportWriter
		assert needyConfiguration.getReportWriters()[0].outputFile == "xxx"
		assert needyConfiguration.getReportWriters()[1] instanceof StubReportWriter
	}
	
	@Test
	void test_getApplicationBuilds_UnknownMethodOutsideApplications() {
		final TEXT = """
			needy {
				// This should not be here; should be within "applications { }"
				Fidget(["http://svn/Fidget/build.gradle", "http://svn/Fidget/build.gradle"])
			}
		"""
		shouldFail(MissingMethodException) { GroovyDslNeedyConfiguration.fromString(TEXT) }
	}
	
	@Test
	void test_InvalidSyntaxOfNeedyConfigFile() {
		final TEXT = "%^&*()GHJ"
		shouldFail(IllegalStateException) { GroovyDslNeedyConfiguration.fromString(TEXT) }
	}
	
	//--------------------------------------------------------------------------
	// Helper methods
	//--------------------------------------------------------------------------
		
	private void assertApplicationBuilds(List<ApplicationBuild> actual, List<Map> expected) {
		expected.eachWithIndex { Map expectedMap, int index ->
			assertApplicationBuild(actual[index], expectedMap)
		}
	}
	
	private void assertApplicationBuild(ApplicationBuild actual, Map expected) {
		assert actual.name == expected.name
		assert actual.buildScripts.size() == expected.urls.size() 
		assert actual.buildScripts.every { buildScript -> buildScript instanceof UrlBuildScript }
		expected.urls.eachWithIndex { String expectedUrl, int index ->
			assert actual.buildScripts[index].url.toString() == expectedUrl
		}
	}

}
