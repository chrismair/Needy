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

class ArtifactTest extends AbstractTestCase {

	@Test
	void test_toString() {
		assert new Artifact(group:"org.hibernate", name:"hibernate", version:"3.1").toString() == "org.hibernate:hibernate:3.1"
		assert new Artifact(group:null, name:"hibernate", version:"3.1").toString() == ":hibernate:3.1"
		assert new Artifact(group:"org.hibernate", name:"hibernate", version:null).toString() == "org.hibernate:hibernate:"
	}
	
}
