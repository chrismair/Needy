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
package org.dx42.needy.parser

import org.dx42.needy.AbstractTestCase
import org.dx42.needy.Dependency
import org.junit.Test

/**
 * Tests for GrailsBuildConfigDependencyParser
 *
 * @author Chris Mair
 */
class GrailsBuildConfigDependencyParserTest extends AbstractTestCase {

	private static final String NAME = "MyApp1"
	private static final Map BINDING = [:]
	
	private parser = new GrailsBuildConfigDependencyParser()
	
	@Test
	void test_ImplementsDependencyParser() {
		assert parser instanceof DependencyParser
	}

	@Test
	void test_parse_NullSource() {
		shouldFail(IllegalArgumentException) { parser.parse(NAME, null, BINDING) }
	}

	@Test
	void test_parse_EmptySource() {
		assert parser.parse(NAME, "", BINDING) == []
	}

	@Test
	void test_parse_EmptyDependenciesClosure() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies { }
			}"""
		assert parser.parse(NAME, SOURCE, BINDING) == []
	}
	
	@Test
	void test_parse_Single() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				compile group: 'org.hibernate', name: 'hibernate-core', version: '3.1'
			}
		}"""
		assert parser.parse(NAME, SOURCE, BINDING) == [new Dependency(applicationName:NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1")]
	}

	@Test
	void test_parse_DifferentConfigurations() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				compile group: 'org.hibernate', name: 'hibernate-core', version: '3.1'
				test group: 'junit', name: 'junit', version: '4.8.1'
				runtime group: 'g1', name: 'n1', version: 'v1'
				build group: 'g2', name: 'n2', version: 'v2'
				provided group: 'g3', name: 'n3', version: 'v3'
			}
		}"""
		assert parser.parse(NAME, SOURCE, BINDING) == [
			new Dependency(applicationName:NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(applicationName:NAME, configuration:"test", group:"junit", name:"junit", version:"4.8.1"),
			new Dependency(applicationName:NAME, configuration:"runtime", group:"g1", name:"n1", version:"v1"),
			new Dependency(applicationName:NAME, configuration:"build", group:"g2", name:"n2", version:"v2"),
			new Dependency(applicationName:NAME, configuration:"provided", group:"g3", name:"n3", version:"v3"),
		]
	}

	@Test
	void test_parse_StringFormat() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				compile "org.hibernate:hibernate-core:3.1"
				test group: 'junit', name: 'junit', version: '4.8.1', transitive:false
			}
		}"""
		assert parser.parse(NAME, SOURCE, BINDING) == [
            new Dependency(applicationName:NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
            new Dependency(applicationName:NAME, configuration:"test", group:"junit", name:"junit", version:"4.8.1")
        ]
	}
	
	@Test
	void test_parse_StringFormat_NameAndVersionOnly() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				runtime ":MyUtil:9"
			}
		}"""
		assert parser.parse(NAME, SOURCE, BINDING) == [new Dependency(applicationName:NAME, configuration:"runtime", group:null, name:"MyUtil", version:"9")]
	}

	// TODO Is name-only valid in "BuildConfig.groovy"?
	@Test
	void test_parse_StringFormat_NameOnly() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				build "MyUtil"
			}
		}"""
		assert parser.parse(NAME, SOURCE, BINDING) == [new Dependency(applicationName:NAME, configuration:"build", group:null, name:"MyUtil", version:null)]
	}

	@Test
	void test_parse_Excludes() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				runtime(group:'com.mysql', name:'mysql-connector-java', version:'5.1.16') {
				    excludes([ group: 'xml-apis', name: 'xml-apis'],
				             [ group: 'org.apache.httpcomponents' ],
				             [ name: 'commons-logging' ])
				}
			}
		}"""
		assert parser.parse(NAME, SOURCE, BINDING) == [new Dependency(applicationName:NAME, configuration:"runtime", group:"com.mysql", name:"mysql-connector-java", version:"5.1.16")]
	}

	@Test
	void test_parse_MultipleStrings_WithClosure() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				runtime('com.mysql:mysql-connector-java:5.1.16',
				        'net.sf.ehcache:ehcache:1.6.1') {
				    transitive = false
				}
			}
		}"""
		assert parser.parse(NAME, SOURCE, BINDING) == [
			new Dependency(applicationName:NAME, configuration:"runtime", group:"com.mysql", name:"mysql-connector-java", version:"5.1.16"),
			new Dependency(applicationName:NAME, configuration:"runtime", group:"net.sf.ehcache", name:"ehcache", version:"1.6.1")]
	}

	@Test
	void test_parse_MultipleDependenciesPerStatement_Strings() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				runtime 'com.mysql:mysql-connector-java:5.1.16',
        			'net.sf.ehcache:ehcache:1.6.1'
			}
		}"""
		assert parser.parse(NAME, SOURCE, BINDING) == [
			new Dependency(applicationName:NAME, configuration:"runtime", group:"com.mysql", name:"mysql-connector-java", version:"5.1.16"),
			new Dependency(applicationName:NAME, configuration:"runtime", group:"net.sf.ehcache", name:"ehcache", version:"1.6.1")
		]
	}
	
	@Test
	void test_parse_MultipleDependenciesPerStatement_Maps() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				runtime(
				    [group:'com.mysql', name:'mysql-connector-java', version:'5.1.16'],
				    [group:'net.sf.ehcache', name:'ehcache', version:'1.6.1']
				)
			}
		}"""
		assert parser.parse(NAME, SOURCE, BINDING) == [
			new Dependency(applicationName:NAME, configuration:"runtime", group:"com.mysql", name:"mysql-connector-java", version:"5.1.16"),
			new Dependency(applicationName:NAME, configuration:"runtime", group:"net.sf.ehcache", name:"ehcache", version:"1.6.1")
		]
	}
	
	@Test
	void test_parse_LocalVariablesUsedWithinDependencySpecification() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				String hibernateVersion = "3.1"
				String junitName = "junit"
				compile "org.hibernate:hibernate-core:\$hibernateVersion"
				test group: 'junit', name: junitName, version: '4.8.1'
			}
		}"""
		assert parser.parse(NAME, SOURCE, BINDING) == [
			new Dependency(applicationName:NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(applicationName:NAME, configuration:"test", group:"junit", name:"junit", version:"4.8.1")
		]
	}

	@Test
	void test_parse_BindingVariablesUsedWithinDependencySpecification() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				compile "org.hibernate:hibernate-core:\$hibernateVersion"
				test group: 'junit', name: junitName, version: '4.8.1'
			}
		}"""
		def binding = [hibernateVersion:"3.1", junitName:"junit"]
		assert parser.parse(NAME, SOURCE, binding) == [
			new Dependency(applicationName:NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(applicationName:NAME, configuration:"test", group:"junit", name:"junit", version:"4.8.1")
		]
	}

	@Test
	void test_parse_UnknownVariableUsedWithinDependencySpecification() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				compile "org.hibernate:hibernate-core:\$hibernateVersion"
				test group: 'junit', name: junitName, version: '4.8.1'
			}
		}"""
		assert parser.parse(NAME, SOURCE, BINDING) == [
			new Dependency(applicationName:NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"?"),
			new Dependency(applicationName:NAME, configuration:"test", group:"junit", name:"?", version:"4.8.1")
		]
	}

	@Test
	void test_parse_RequiredVariableUsedWithinOuterScript_DefinedInBinding() {
		final SOURCE = """
			new File("\$testsrc/test-config.txt").withReader { }
			grails.project.dependency.resolution = {
				dependencies { }
			}"""
		def binding = [testsrc:"src/test/resources"]
		assert parser.parse(NAME, SOURCE, binding) == []
	}

	@Test
	void test_parse_RequiredButUndefinedVariableUsedWithinOuterScript() {
		final SOURCE = """grails.project.dependency.resolution = {
			new File("\$userHome/.gradle/gradle.properties").withReader { props.load(it) }
			dependencies {
				compile "org.hibernate:hibernate-core:1.0"
			}
		}"""
		shouldFail(FileNotFoundException) { parser.parse(NAME, SOURCE, BINDING) }
	}
		
	@Test
	void test_parse_OtherContents() {
		final SOURCE = """
			// Cobertura (see http://grails.org/plugin/code-coverage)
			coverage {
			    exclusions = ["BuildConfig*", "**/test/**", "org/**"]
			}

			grails.project.dependency.resolution = {
				dependencies {
				}
			}"""
		assert parser.parse(NAME, SOURCE, BINDING) == []
	}

	@Test
	void test_parse_FullGrailsBuildConfigFile() {
		String source = new File('src/test/resources/sample1-grails-buildconfig.txt').text
		def dependencies = parser.parse(NAME, source, BINDING)
		assert dependencies.size() == 11
		assert dependencies[0] == new Dependency(applicationName:"MyApp1", group:"mydb", name:"client", version:"16.0.EBF26086", configuration:"runtime") 
		assert dependencies[1] == new Dependency(applicationName:"MyApp1", group:"acme", name:"util", version:"1.0", configuration:"runtime") 
		assert dependencies[2] == new Dependency(applicationName:"MyApp1", group:"commons-dbcp", name:"commons-dbcp", version:"1.4", configuration:"build") 
		assert dependencies[3] == new Dependency(applicationName:"MyApp1", group:"acme", name:"architecture", version:"3.29.0", configuration:"compile")
		assert dependencies[4] == new Dependency(applicationName:"MyApp1", group:"commons-collections", name:"commons-collections", version:"3.2.2", configuration:"compile") 
		assert dependencies[5] == new Dependency(applicationName:"MyApp1", group:"javax.validation", name:"validation-api", version:"1.1.0.Final", configuration:"compile")
		assert dependencies[6] == new Dependency(applicationName:"MyApp1", group:"org.springframework", name:"spring-orm", version:"?", configuration:"compile")
		assert dependencies[7] == new Dependency(applicationName:"MyApp1", group:"org.springframework", name:"spring-aop", version:"4.0.5.RELEASE", configuration:"compile") 
		assert dependencies[8] == new Dependency(applicationName:"MyApp1", group:"org.springframework", name:"spring-expression", version:"4.0.5.RELEASE", configuration:"compile") 
		assert dependencies[9] == new Dependency(applicationName:"MyApp1", group:"org.hamcrest", name:"hamcrest-core", version:"1.3", configuration:"test")
		assert dependencies[10] == new Dependency(applicationName:"MyApp1", group:"org.hsqldb", name:"hsqldb", version:"2.3.2", configuration:"test")
	}

	@Test
	void test_parse_FullGrailsBuildConfigFile_2() {
		String source = new File('src/test/resources/sample2-grails-buildconfig.txt').text
		def dependencies = parser.parse(NAME, source, BINDING)
		assert dependencies.size() == 2
		assert dependencies[0] == new Dependency(applicationName:"MyApp1", group:"mysql", name:"mysql-connector-java", version:"5.1.24", configuration:"runtime") 
		assert dependencies[1] == new Dependency(applicationName:"MyApp1", group:"org.springframework.integration", name:"spring-integration-core", version:"2.2.5.RELEASE", configuration:"compile") 
	}

	@Test
	void test_parse_FullGrailsBuildConfigFile_3() {
		String source = new File('src/test/resources/sample3-grails-buildconfig.txt').text
		def dependencies = parser.parse(NAME, source, BINDING)
		assert dependencies.size() == 13
	}

	// Tests for invalid DSL syntax/format
	
	@Test
	void test_parse_InvalidConfigurationName() {
		final SOURCE = """grails.project.dependency.resolution = {
				dependencies { custom 'log4j:log4:1.17' }
			}"""
		shouldFail { parser.parse(NAME, SOURCE, BINDING) }
	}

	@Test
	void test_parse_StringFormat_Empty() {
		final SOURCE = """grails.project.dependency.resolution = {
			dependencies {
				compile ""
			}
		}"""
		shouldFailWithMessage("empty") { parser.parse(NAME, SOURCE, BINDING) }
	}
	
}
