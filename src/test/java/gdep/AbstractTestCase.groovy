package gdep

import org.apache.log4j.Logger
import org.junit.After
import org.junit.Rule
import org.junit.rules.TestName

class AbstractTestCase {

	@SuppressWarnings('FieldName')
	protected final LOG = Logger.getLogger(getClass())

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
