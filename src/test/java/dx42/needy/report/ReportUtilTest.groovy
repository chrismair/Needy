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
package dx42.needy.report

import org.junit.Test

import dx42.needy.AbstractTestCase
import dx42.needy.Artifact
import dx42.needy.Dependency

class ReportUtilTest extends AbstractTestCase {

	private static final String FILE_ON_CLASSPATH = "htmlreport.css"
	private static final String FILE_PATH = "src/main/resources/$FILE_ON_CLASSPATH"
	
	@Test
	void test_buildMapOfArtifactToApplicationNames_EmptyDependencies() {
		def dependencies = []

		def map = ReportUtil.buildMapOfArtifactToApplicationNames(dependencies)
		assert map == [:]
	}
	
	@Test
	void test_buildMapOfArtifactToApplicationNames_SingleDependency() {
		def dependencies = [
			new Dependency(applicationName:"Third", group:"org.other", name:"service", version:"2.0"),
		]

		def map = ReportUtil.buildMapOfArtifactToApplicationNames(dependencies)
		assert map == [(new Artifact(group:"org.other", name:"service", version:"2.0")): ["Third"] as SortedSet]
	}
	
	@Test
	void test_buildMapOfArtifactToApplicationNames() {
		def dependencies = [
			new Dependency(applicationName:"Third", group:"org.other", name:"service", version:"2.0"),
			new Dependency(applicationName:"Sample1", group:"ORG.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(applicationName:"Sample_Two", group:"log4j", name:"log4j", version:"1.2.14"),
			new Dependency(applicationName:"Sample1", group:"log4j", name:"log4j", version:"1.2.14"),
			new Dependency(applicationName:"Third", group:"log4j", name:"log4j", version:"1.2.14"),
			new Dependency(applicationName:"Third", group:"log4j-extra", name:"stuff", version:"1.0"),
		]

		def map = ReportUtil.buildMapOfArtifactToApplicationNames(dependencies)

		def a1 = new Artifact(group:"log4j", name:"log4j", version:"1.2.14")
		def a2 = new Artifact(group:"log4j-extra", name:"stuff", version:"1.0") 
		def a3 = new Artifact(group:"ORG.hibernate", name:"hibernate-core", version:"3.1")
		def a4 = new Artifact(group:"org.other", name:"service", version:"2.0")
				
		// Verify sort order of keys
		assert map.keySet() as List == [a1, a2, a3, a4]
		
		assert map == [
			(a1): ["Sample1", "Sample_Two", "Third"] as SortedSet,
			(a2): ["Third"] as SortedSet,
			(a3): ["Sample1"] as SortedSet,
			(a4): ["Third"] as SortedSet]
	}
	
	@Test
	void test_getClasspathFileInputStream() {
		def fileText = new File(FILE_PATH).text
		def inputStream = ReportUtil.getClasspathFileInputStream(FILE_ON_CLASSPATH)
		assert inputStream instanceof InputStream
		assert inputStream.text == fileText
	}
	
	@Test
	void test_getClasspathFileInputStream_FileNotFound() {
		shouldFail(FileNotFoundException) { ReportUtil.getClasspathFileInputStream("BadDir/NotFound") }
	}
	
}
