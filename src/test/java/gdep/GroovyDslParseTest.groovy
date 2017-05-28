package gdep;

import static groovy.test.GroovyAssert.*

import org.junit.Test

class GroovyDslParseTest extends AbstractTestCase {

	private parser = new GroovyDslParser()
	
	@Test
	public void test_parse_Null() {
		shouldFail(IllegalArgumentException) { parser.parse(null) }
	}

	@Test
	public void test_parse_Empty() {
		//def CLOSURE = {  }
		//assert parser.parse(CLOSURE) == []
		assert parser.parse("") == []
	}

	@Test
	public void test_parse_Single() {
		def SOURCE = """dependencies {
			compile group: 'org.hibernate', name: 'hibernate-core', version: '3.1'
		}"""
		assert parser.parse(SOURCE) == [new Dependency(configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1")]
	}

	@Test
	public void test_parse_Multiple_DifferentConfigurations() {
		def SOURCE = """dependencies {
			compile group: 'org.hibernate', name: 'hibernate-core', version: '3.1'
			testCompile group: 'junit', name: 'junit', version: '4.8.1'
		}"""
		assert parser.parse(SOURCE) == [
			new Dependency(configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(configuration:"testCompile", group:"junit", name:"junit", version:"4.8.1")
		]
	}

}
