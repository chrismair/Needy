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
