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
package needy

import org.junit.Test

class NeedyTest extends AbstractTestCase {

	private static NeedyConfiguration APPLICATION_BUILD_SET = [:] as NeedyConfiguration
	private static final String TEST_CONFIG_FILE = new File('src/test/resource/test-build-set.txt')
	
	private Needy needy = new Needy()
	
	@Test
	void test_execute() {
		def called = [:]
		def needyConfiguration
		def reportWriters
		needy.createNeedyRunner = { 
			return [
				execute:{ called.execute = true	},
				setNeedyConfiguration:{ abs -> needyConfiguration = abs },
				setReportWriters: { rw -> reportWriters = rw } ] 
		}
		needy.createNeedyConfiguration = { filename ->
			assert filename == Needy.DEFAULT_CONFIG_FILE
			return APPLICATION_BUILD_SET
		}
		
		needy.execute([] as String[])
		
		assert called.execute
		assert needyConfiguration == APPLICATION_BUILD_SET
		assert reportWriters.size() == 1
		assert reportWriters[0] instanceof ByArtifactTextReportWriter
	}

	// TODO Test for main()
		
	@Test
	void test_createNeedyRunner() {
		assert needy.createNeedyRunner() instanceof NeedyRunner
	}
	
}