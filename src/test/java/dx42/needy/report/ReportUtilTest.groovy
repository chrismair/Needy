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
import dx42.needy.Dependency

class ReportUtilTest extends AbstractTestCase {

	@Test
	void test_buildMapOfArtifactNameToApplicationNames_EmptyDependencies() {
		def dependencies = []

		def map = ReportUtil.buildMapOfArtifactNameToApplicationNames(dependencies)
		assert map == [:]
	}
	
	@Test
	void test_buildMapOfArtifactNameToApplicationNames_SingleDependency() {
		def dependencies = [
			new Dependency(applicationName:"Third", group:"org.other", name:"service", version:"2.0"),
		]

		def map = ReportUtil.buildMapOfArtifactNameToApplicationNames(dependencies)
		assert map == ["org.other:service:2.0": ["Third"] as SortedSet]
	}
	
	@Test
	void test_buildMapOfArtifactNameToApplicationNames() {
		def dependencies = [
			new Dependency(applicationName:"Third", group:"org.other", name:"service", version:"2.0"),
			new Dependency(applicationName:"Sample1", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(applicationName:"Sample_Two", group:"log4j", name:"log4j", version:"1.2.14"),
			new Dependency(applicationName:"Sample1", group:"log4j", name:"log4j", version:"1.2.14"),
			new Dependency(applicationName:"Third", group:"log4j", name:"log4j", version:"1.2.14"),
			new Dependency(applicationName:"Third", group:"log4j-extra", name:"stuff", version:"1.0"),
		]

		def map = ReportUtil.buildMapOfArtifactNameToApplicationNames(dependencies)
		
		// Verify sort order of keys
		assert map.keySet() == ["log4j:log4j:1.2.14", "log4j-extra:stuff:1.0", "org.hibernate:hibernate-core:3.1", "org.other:service:2.0"] as Set
		
		assert map == [
			"log4j:log4j:1.2.14": ["Sample1", "Sample_Two", "Third"] as SortedSet,
			"log4j-extra:stuff:1.0": ["Third"] as SortedSet,
			"org.hibernate:hibernate-core:3.1": ["Sample1"] as SortedSet,
			"org.other:service:2.0": ["Third"] as SortedSet]
	}
	
}
