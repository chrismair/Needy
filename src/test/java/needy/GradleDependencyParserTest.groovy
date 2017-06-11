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
package needy;

import org.junit.Test

import needy.Dependency
import needy.GradleDependencyParser

class GradleDependencyParserTest extends AbstractTestCase {

	private static final String APPLICATION_NAME = "MyApp1"
	
	private parser = new GradleDependencyParser(APPLICATION_NAME)
	
	@Test
	void test_ImplementsDependencyParser() {
		assert parser instanceof DependencyParser
	}

	@Test
	void test_parse_Null() {
		shouldFail(IllegalArgumentException) { parser.parse(null) }
	}

	@Test
	void test_parse_EmptyString() {
		assert parser.parse("") == []
	}

	@Test
	void test_parse_EmptyDependenciesClosure() {
		assert parser.parse("dependencies { }") == []
	}
	
	@Test
	void test_parse_Single() {
		final SOURCE = """dependencies {
			compile group: 'org.hibernate', name: 'hibernate-core', version: '3.1'
		}"""
		assert parser.parse(SOURCE) == [new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1")]
	}

	@Test
	void test_parse_DifferentConfigurations() {
		final SOURCE = """dependencies {
			compile group: 'org.hibernate', name: 'hibernate-core', version: '3.1'
			testCompile group: 'junit', name: 'junit', version: '4.8.1'
		}"""
		assert parser.parse(SOURCE) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"testCompile", group:"junit", name:"junit", version:"4.8.1")
		]
	}

	@Test
	void test_parse_StringFormat() {
		final SOURCE = """dependencies {
			compile "org.hibernate:hibernate-core:3.1"
			testCompile group: 'junit', name: 'junit', version: '4.8.1'
		}"""
		assert parser.parse(SOURCE) == [
            new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
            new Dependency(applicationName:APPLICATION_NAME, configuration:"testCompile", group:"junit", name:"junit", version:"4.8.1")
        ]
	}
	
	@Test
	void test_parse_StringFormat_NameAndVersionOnly() {
		final SOURCE = """dependencies {
			runtime "MyUtil:9"
		}"""
		assert parser.parse(SOURCE) == [new Dependency(applicationName:APPLICATION_NAME, configuration:"runtime", group:null, name:"MyUtil", version:"9")]
	}

	@Test
	void test_parse_StringFormat_NameOnly() {
		final SOURCE = """dependencies {
			other "MyUtil"
		}"""
		assert parser.parse(SOURCE) == [new Dependency(applicationName:APPLICATION_NAME, configuration:"other", group:null, name:"MyUtil", version:null)]
	}

	@Test
	void test_parse_VariablesUsedWithinDependencySpecification() {
		final SOURCE = """dependencies {
			String hibernateVersion = "3.1"
			String junitName = "junit"
			compile "org.hibernate:hibernate-core:\$hibernateVersion"
			testCompile group: 'junit', name: junitName, version: '4.8.1'
		}"""
		assert parser.parse(SOURCE) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"testCompile", group:"junit", name:"junit", version:"4.8.1")
		]
	}

	@Test
	void test_parse_UnknownVariableUsedWithinDependencySpecification() {
		final SOURCE = """dependencies {
			compile "org.hibernate:hibernate-core:\$hibernateVersion"
		}"""
		assert parser.parse(SOURCE) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"?")
		]
	}

	@Test
	void test_parse_FullGradleBuildFile() {
		final SOURCE = """
			plugins {
			    id 'groovy'
			}
			
			sourceCompatibility = '1.6'
			targetCompatibility = '1.6'

			def hibernateVersion = "3.1"
			
			repositories {
			     maven { url "http://repo.maven.apache.org/maven2" }
			}

			dependencies {
				compile group: 'org.hibernate', name: 'hibernate-core', version: hibernateVersion
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
		assert parser.parse(SOURCE) == [new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1")]
	}

	// Tests for other dependency options and formats
	
	@Test
	void test_parse_OtherProperties() {
		final SOURCE = """dependencies {
			compile group: 'org.h7', name: 'h7', version: '3.1', transitive:false, ext: 'jar', classifier:'jdk17', configuration:'c1'
			runtime "org.groovy:groovy:2.2.0@jar"
			compile "org.other:service:1.0:jdk15@jar"
		}"""
		assert parser.parse(SOURCE) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.h7", name:"h7", version:"3.1"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"runtime", group:"org.groovy", name:"groovy", version:"2.2.0"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.other", name:"service", version:"1.0")
		]
	}

	@Test
	void test_parse_MultipleDependenciesPerStatement() {
		final SOURCE = """dependencies {
			runtime(
			        [group: 'org.springframework', name: 'spring-core', version: '2.5'],
			        [group: 'org.springframework', name: 'spring-aop', version: '2.5']
			    )

			sealife "sea.mammals:orca:1.0", "sea.fish:shark:1.0", "sea.fish:tuna:1.0"
		}"""
		assert parser.parse(SOURCE) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"runtime", group:"org.springframework", name:"spring-core", version:"2.5"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"runtime", group:"org.springframework", name:"spring-aop", version:"2.5"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"sealife", group:"sea.mammals", name:"orca", version:"1.0"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"sealife", group:"sea.fish", name:"shark", version:"1.0"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"sealife", group:"sea.fish", name:"tuna", version:"1.0"),
		]
	}
	
	@Test
	void test_parse_SpecifyDependenciesInAVariable_List() {
		final SOURCE = """dependencies {
			List groovy = ["org.codehaus.groovy:groovy-all:2.4.10@jar",
			               "commons-cli:commons-cli:1.0@jar",
			               "org.apache.ant:ant:1.9.6@jar"]
			runtime groovy
		}"""
		assert parser.parse(SOURCE) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"runtime", group:"org.codehaus.groovy", name:"groovy-all", version:"2.4.10"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"runtime", group:"commons-cli", name:"commons-cli", version:"1.0"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"runtime", group:"org.apache.ant", name:"ant", version:"1.9.6"),
		]
	}

	@Test
	void test_parse_SpecifyDependenciesInAVariable_MultipleLists() {
		final SOURCE = """dependencies {
			List groovy = ["org.codehaus.groovy:groovy-all:2.4.10@jar", "commons-cli:commons-cli:1.0@jar"]
			List hibernate = ['org.hibernate:hibernate:3.0.5@jar']		    
			runtime groovy, hibernate
		}"""
		assert parser.parse(SOURCE) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"runtime", group:"org.codehaus.groovy", name:"groovy-all", version:"2.4.10"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"runtime", group:"commons-cli", name:"commons-cli", version:"1.0"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"runtime", group:"org.hibernate", name:"hibernate", version:"3.0.5"),
		]
	}

	@Test
	void test_parse_OtherSyntax() {
		final SOURCE = """dependencies {
			compile("org.gradle:api:1.0") {
				exclude module: 'shared'
			}

			alllife configurations.sealife						// ignored

			compile project(':shared')							// ignored
			runtime files('libs/a.jar', 'libs/b.jar')			// ignored
			runtime fileTree(dir: 'libs', include: '*.jar')		// ignored
			compile files("build/classes") {					// ignored
        		builtBy 'compile'
			}
			compile gradleApi()									// ignored
			compile localGroovy()								// ignored
		}"""
		assert parser.parse(SOURCE) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.gradle", name:"api", version:"1.0"),
		]
	}
	
	// Tests for invalid DSL syntax/format
	
	@Test
	void test_parse_StringFormat_Empty() {
		shouldFail { parser.parse("dependencies { compile '' }") }	
	}

	@Test
	void test_parse_IllegalGroovySyntax() {
		shouldFail(IllegalStateException) { parser.parse("%^@ &*[]") }	
	}
	
}
