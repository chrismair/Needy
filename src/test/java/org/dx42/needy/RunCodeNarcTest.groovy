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

class RunCodeNarcTest extends AbstractTestCase {

	private static final GROOVY_FILES = '**/*.groovy'
	private static final RULESET_FILE = "codenarc.ruleset"
			
	@Test
	void runCodeNarc() {
		def ant = new AntBuilder()
		
		ant.taskdef(name:'codenarc', classname:'org.codenarc.ant.CodeNarcTask')

		ant.codenarc(ruleSetFiles:RULESET_FILE,
		   maxPriority1Violations:0, maxPriority2Violations:0, maxPriority3Violations:0) {

		   fileset(dir:'src/main/java') {
			   include(name:GROOVY_FILES)
		   }
		   fileset(dir:'src/test/java') {
			   include(name:GROOVY_FILES)
		   }

		   report(type:'ide')
		}
	}
	
}
