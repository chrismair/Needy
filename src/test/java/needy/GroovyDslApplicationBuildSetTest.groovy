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

import static groovy.test.GroovyAssert.*

import org.junit.Test

class GroovyDslApplicationBuildSetTest extends AbstractTestCase {

	private static final TEST_BUILD_SET_FILE = "src/test/resources/test-build-set.txt"
	
	@Test
	void test_fromString_NullOrEmptyString() {
		shouldFailWithMessage("text") { GroovyDslApplicationBuildSet.fromString(null) }
		shouldFailWithMessage("text") { GroovyDslApplicationBuildSet.fromString("") }
	}

	@Test
	void test_fromFile_NullOrEmptyFilename() {
		shouldFailWithMessage("file") { GroovyDslApplicationBuildSet.fromFile(null) }
		shouldFailWithMessage("file") { GroovyDslApplicationBuildSet.fromFile("") }
	}

	@Test
	void test_fromFile_FileDoesNotExist() {
		shouldFail(IOException) { GroovyDslApplicationBuildSet.fromFile("NoSuchFile.txt") }
	}

	@Test
	void test_fromFile_ReadsInFileText() {
		def buildSet = GroovyDslApplicationBuildSet.fromFile(TEST_BUILD_SET_FILE)
		assert buildSet.getText() == new File(TEST_BUILD_SET_FILE).text
	}

	@Test
	void test_getApplicationBuilds_EmptyNeedyClosure() {
		def applicationBuildSet = GroovyDslApplicationBuildSet.fromString("needy { }")
		assert applicationBuildSet.getApplicationBuilds() == []
	}

	@Test
	void test_getApplicationBuilds_SingleApplication() {
		final TEXT = """
			needy {
				Fidget("http://svn/Fidget/build.gradle")
			}
		"""
		def applicationBuildSet = GroovyDslApplicationBuildSet.fromString(TEXT)
		assertApplicationBuilds(applicationBuildSet.getApplicationBuilds(), [[name:"Fidget", urls:["http://svn/Fidget/build.gradle"]]]) 
	}
	
	@Test
	void test_getApplicationBuilds_MultipleApplications() {
		final TEXT = """
			needy {
				Fidget("http://svn/Fidget/build.gradle")
				Wallace("http://svn/Wallace/custom-build.gradle")
			}
		"""
		def applicationBuildSet = GroovyDslApplicationBuildSet.fromString(TEXT)
		assertApplicationBuilds(applicationBuildSet.getApplicationBuilds(), [
			[name:"Fidget", urls:["http://svn/Fidget/build.gradle"]], 
			[name:"Wallace", urls:["http://svn/Wallace/custom-build.gradle"]]]) 
	}
	
	@Test
	void test_getApplicationBuilds_MultipleUrlsPerApplication() {
		final TEXT = """
			needy {
				Fidget(["http://svn/Fidget/build.gradle", "http://svn/Fidget2/build2.gradle"])
			}
		"""
		def applicationBuildSet = GroovyDslApplicationBuildSet.fromString(TEXT)
		assertApplicationBuilds(applicationBuildSet.getApplicationBuilds(), [
			[name:"Fidget", urls:["http://svn/Fidget/build.gradle", "http://svn/Fidget2/build2.gradle"]]]) 
	}
	
	@Test
	void test_getApplicationBuilds_InvalidSyntaxOfFile() {
		final TEXT = "%^&*()GHJ"
		def applicationBuildSet = GroovyDslApplicationBuildSet.fromString(TEXT)
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
