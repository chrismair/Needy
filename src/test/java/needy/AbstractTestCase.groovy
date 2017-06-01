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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.junit.After
import org.junit.Rule
import org.junit.rules.TestName

class AbstractTestCase {

	@SuppressWarnings('FieldName')
	protected final LOG = LoggerFactory.getLogger(getClass())

	@SuppressWarnings('PublicInstanceField')
	@Rule public TestName testName = new TestName()

	
	@After
	void afterAbstractTestCase() {
        log "----------[ ${getClass().getSimpleName()}.${getName()} ]----------"
	}
	
	protected void log(message) {
		LOG.info message
	}

	protected String getName() {
		return testName.getMethodName()
	}

}
