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
	public void test_parse_EmptyString() {
		assert parser.parse("") == []
	}

	@Test
	public void test_parse_EmptyDependenciesClosure() {
		assert parser.parse("dependencies { }") == []
	}
	
	@Test
	public void test_parse_Single() {
		final SOURCE = """dependencies {
			compile group: 'org.hibernate', name: 'hibernate-core', version: '3.1'
		}"""
		assert parser.parse(SOURCE) == [new Dependency(configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1")]
	}

	@Test
	public void test_parse_Multiple_DifferentConfigurations() {
		final SOURCE = """dependencies {
			compile group: 'org.hibernate', name: 'hibernate-core', version: '3.1'
			testCompile group: 'junit', name: 'junit', version: '4.8.1'
		}"""
		assert parser.parse(SOURCE) == [
			new Dependency(configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(configuration:"testCompile", group:"junit", name:"junit", version:"4.8.1")
		]
	}

	@Test
	public void test_parse_FullGradleBuildFile() {
		final SOURCE = """
			plugins {
			    id 'groovy'
			}
			
			sourceCompatibility = '1.6'
			targetCompatibility = '1.6'
			
			repositories {
			     maven { url "http://repo.maven.apache.org/maven2" }
			}

			dependencies {
				compile group: 'org.hibernate', name: 'hibernate-core', version: '3.1'
			}

			test {
			    testLogging {
			        events 'passed', 'skipped', 'failed'
			    }
			}
			
			task javadocJar(type: Jar) {
			    classifier = 'javadoc'
			    from javadoc
			}
		"""
		assert parser.parse(SOURCE) == [new Dependency(configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1")]
	}

}
