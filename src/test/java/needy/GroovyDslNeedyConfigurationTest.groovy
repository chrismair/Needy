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
package needy;

import org.junit.Test

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
	void test_getApplicationBuilds_EmptyNeedyClosure() {
		def applicationBuildSet = GroovyDslNeedyConfiguration.fromString("needy { }")
		assert applicationBuildSet.getApplicationBuilds() == []
	}

	@Test
	void test_getApplicationBuilds_SingleApplication() {
		final TEXT = """
			needy {
				applications {
					Fidget("http://svn/Fidget/build.gradle")
				}
			}
		"""
		def applicationBuildSet = GroovyDslNeedyConfiguration.fromString(TEXT)
		assertApplicationBuilds(applicationBuildSet.getApplicationBuilds(), [[name:"Fidget", urls:["http://svn/Fidget/build.gradle"]]]) 
	}
	
	@Test
	void test_getApplicationBuilds_MultipleApplications() {
		final TEXT = """
			needy {
				applications {
					Fidget("http://svn/Fidget/build.gradle")
					Wallace("http://svn/Wallace/custom-build.gradle")
				}
			}
		"""
		def applicationBuildSet = GroovyDslNeedyConfiguration.fromString(TEXT)
		assertApplicationBuilds(applicationBuildSet.getApplicationBuilds(), [
			[name:"Fidget", urls:["http://svn/Fidget/build.gradle"]], 
			[name:"Wallace", urls:["http://svn/Wallace/custom-build.gradle"]]]) 
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
		def applicationBuildSet = GroovyDslNeedyConfiguration.fromString(TEXT)
		assertApplicationBuilds(applicationBuildSet.getApplicationBuilds(), [
			[name:"Fidget", urls:["http://svn/Fidget/build.gradle", "http://svn/Fidget2/build2.gradle"]]]) 
	}
	
	@Test
	void test_getApplicationBuilds_UnknownMethodOutsideApplications() {
		final TEXT = """
			needy {
				// This should not be here; should be within "applications { }"
				Fidget(["http://svn/Fidget/build.gradle", "http://svn/Fidget/build.gradle"])
			}
		"""
		def applicationBuildSet = GroovyDslNeedyConfiguration.fromString(TEXT)
		shouldFail(MissingMethodException) { applicationBuildSet.getApplicationBuilds() }
	}
	
	@Test
	void test_getApplicationBuilds_InvalidSyntaxOfFile() {
		final TEXT = "%^&*()GHJ"
		def applicationBuildSet = GroovyDslNeedyConfiguration.fromString(TEXT)
		shouldFail(IllegalStateException) { applicationBuildSet.getApplicationBuilds() }
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
