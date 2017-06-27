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

class UrlBuildScriptTest extends AbstractTestCase {

	private static final String TEST_FILE = "src/test/resources/TestFile.txt"
	private static final String TEST_FILE_URL = "file:" + TEST_FILE
	
	@Test
	void test_Constructor_String_Null() {
		shouldFailWithMessage('urlString') { new UrlBuildScript(null) }
	}
	
	@Test
	void test_getText_String_FileDoesNotExist() {
		def buildScript = new UrlBuildScript("file:///NoSuchFile.txt")
		shouldFailWithMessage(FileNotFoundException, 'NoSuchFile.txt') { buildScript.getText() }
	}
	
	@Test
	void test_getText() {
		def buildScript = new UrlBuildScript(TEST_FILE_URL)
		assert buildScript.getText() == new File(TEST_FILE).text
		assert buildScript.getUrl() == new URL(TEST_FILE_URL)
	}
	
	@Test
	void test_toString() {
		def buildScript = new UrlBuildScript(TEST_FILE_URL)
		assert buildScript.toString() == "UrlBuildScript(url=$TEST_FILE_URL)"
	}
	
}
