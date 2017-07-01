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

class NeedyTest extends AbstractTestCase {

	private static final NeedyConfiguration NEEDY_CONFIGURATION = [:] as NeedyConfiguration
	
	private Needy needy = new Needy()
	
	@Test
	void test_execute() {
		def called = [:]
		def needyConfiguration
		needy.createNeedyRunner = { 
			return [
				execute:{ called.execute = true	},
				setNeedyConfiguration:{ abs -> needyConfiguration = abs },
				] 
		}
		needy.createNeedyConfiguration = { filename ->
			assert filename == Needy.DEFAULT_CONFIG_FILE
			return NEEDY_CONFIGURATION
		}
		
		needy.execute([] as String[])
		
		assert called.execute
		assert needyConfiguration == NEEDY_CONFIGURATION
	}

	// TODO Test for main()
		
	@Test
	void test_createNeedyRunner() {
		assert needy.createNeedyRunner() instanceof NeedyRunner
	}
	
}
