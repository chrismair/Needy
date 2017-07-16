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

import org.dx42.needy.Dependency
import org.junit.Test

import org.dx42.needy.AbstractTestCase

class GradleDependencyParserTest extends AbstractTestCase {

	private static final String APPLICATION_NAME = "MyApp1"
	private static final Map BINDING = [:]
	
	private GradleDependencyParser parser = new GradleDependencyParser()
	
	@Test
	void test_ImplementsDependencyParser() {
		assert parser instanceof DependencyParser
	}

	@Test
	void test_parse_NullSource() {
		shouldFail(IllegalArgumentException) { parser.parse(APPLICATION_NAME, null, BINDING) }
	}

	@Test
	void test_parse_EmptySource() {
		assert parser.parse(APPLICATION_NAME, "", BINDING) == []
	}

	@Test
	void test_parse_EmptyDependenciesClosure() {
		assert parser.parse(APPLICATION_NAME, "dependencies { }", BINDING) == []
	}
	
	@Test
	void test_parse_Single() {
		final SOURCE = """dependencies {
			compile group: 'org.hibernate', name: 'hibernate-core', version: '3.1'
		}"""
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1")]
	}

	@Test
	void test_parse_DifferentConfigurations() {
		final SOURCE = """dependencies {
			compile group: 'org.hibernate', name: 'hibernate-core', version: '3.1'
			testCompile group: 'junit', name: 'junit', version: '4.8.1'
		}"""
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [
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
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [
            new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
            new Dependency(applicationName:APPLICATION_NAME, configuration:"testCompile", group:"junit", name:"junit", version:"4.8.1")
        ]
	}
	
	@Test
	void test_parse_StringFormat_NameAndVersionOnly() {
		final SOURCE = """dependencies {
			runtime "MyUtil:9"
		}"""
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [new Dependency(applicationName:APPLICATION_NAME, configuration:"runtime", group:null, name:"MyUtil", version:"9")]
	}

	@Test
	void test_parse_StringFormat_NameOnly() {
		final SOURCE = """dependencies {
			other "MyUtil"
		}"""
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [new Dependency(applicationName:APPLICATION_NAME, configuration:"other", group:null, name:"MyUtil", version:null)]
	}

	@Test
	void test_parse_LocalVariablesUsedWithinDependencySpecification() {
		final SOURCE = """dependencies {
			String hibernateVersion = "3.1"
			String junitName = "junit"
			compile "org.hibernate:hibernate-core:\$hibernateVersion"
			testCompile group: 'junit', name: junitName, version: '4.8.1'
		}"""
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"testCompile", group:"junit", name:"junit", version:"4.8.1")
		]
	}

	@Test
	void test_parse_BindingVariablesUsedWithinDependencySpecification() {
		final SOURCE = """dependencies {
			compile "org.hibernate:hibernate-core:\$hibernateVersion"
			testCompile group: 'junit', name: junitName, version: '4.8.1'
		}"""
		def binding = [hibernateVersion:"3.1", junitName:"junit"]
		assert parser.parse(APPLICATION_NAME, SOURCE, binding) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"testCompile", group:"junit", name:"junit", version:"4.8.1")
		]
	}

	@Test
	void test_parse_UnknownVariableUsedWithinDependencySpecification() {
		final SOURCE = """dependencies {
			compile "org.hibernate:hibernate-core:\$hibernateVersion"
			testCompile group: 'junit', name: junitName, version: '4.8.1'
		}"""
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"?"),
			new Dependency(applicationName:APPLICATION_NAME, configuration:"testCompile", group:"junit", name:"?", version:"4.8.1")
		]
	}

	@Test
	void test_parse_RequiredVariableUsedWithinOuterScript_DefinedInBinding() {
		final SOURCE = """
			new File("\$testsrc/test-config.txt").withReader { props.load(it) }
			dependencies { }
			"""
		def binding = [testsrc:"src/test/resources"]
		assert parser.parse(APPLICATION_NAME, SOURCE, binding) == []
	}

	@Test
	void test_parse_RequiredButUndefinedVariableUsedWithinOuterScript() {
		final SOURCE = """
			new File("\$userHome/.gradle/gradle.properties").withReader { props.load(it) }
			dependencies {
				compile "org.hibernate:hibernate-core:1.0"
			}"""
		shouldFail(FileNotFoundException) { parser.parse(APPLICATION_NAME, SOURCE, BINDING) }
	}

	@Test
	void test_parse_FullGradleBuildFile() {
		final SOURCE = """
			plugins {
			    id 'groovy'
			}
			
			sourceCompatibility = '1.6'
			targetCompatibility = '1.6'

			ext.includeOtherStuff = true

			def hibernateVersion = "3.1"
			
			repositories {
			     maven { url "http://repo.maven.apache.org/maven2" }
			}

			dependencies {
				compile group: 'org.hibernate', name: 'hibernate-core', version: hibernateVersion
			}

			test.maxParallelForks = 2

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
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.hibernate", name:"hibernate-core", version:"3.1")]
	}

	@Test
	void test_parse_FullGradleBuildFile_WithVariousMethodsAndProperties() {
		final SOURCE = '''
			final NEXUS_REPO = "http://some-nexus.acme.com:8081/nexus/content/repositories/releases"
			final BASE_NAME = 'SomeApplication'
			 
			// Configure repositories included from irt-common plugin
			ext.includeNexusReleasesRepository = true
			ext.includeNexusThirdPartyRepository = true
			 
			apply plugin: 'groovy'
			apply plugin: 'pmd'
			apply plugin: 'war'
			apply plugin: 'eclipse'
			 
			webAppDirName = 'WebContent'
			earBaseName = BASE_NAME + 'EAR'
			 
			sourceCompatibility = 1.8
			targetCompatibility = 1.8
			 
			// Artifactory Plugin
			buildscript {
			    repositories {
			        jcenter()                                   
			        maven { url "http://jcenter.bintray.com" }  
			    }
			    dependencies {
			        classpath "org.jfrog.buildinfo:build-info-extractor-gradle:4+"
			    }
			}
			 
			// Custom configuration to pull in extra jars
			configurations { warLibs }
			 
			dependencies {
			    compile group:'org.codehaus.groovy', name:'groovy-all', version:'2.3.9'
			    compile group: 'commons-codec', name: 'commons-codec', version: '1.6'
			    compile group: 'commons-configuration', name: 'commons-configuration', version: '1.6'
			    compile group: 'commons-lang', name: 'commons-lang', version: '2.4'
			    compile group: 'org.apache.httpcomponents', name: 'httpclient', version: '4.2.5'
			    compile group:'log4j', name:'log4j', version:'1.2.17'
			    compile group:'com.google.guava', name:'guava', version:'14.0.1'
			    compile group: 'org.commonjava.googlecode.markdown4j', name: 'markdown4j', version: '2.2-cj-1.1'
			    compile group: 'org.slf4j', name: 'slf4j-log4j12', version: '1.7.10'
			    compile group: 'javax.activation', name: 'activation', version: '1.1.1'
			    compile group: 'javax.validation', name: 'validation-api', version: '1.1.0.Final'
			    compile group: 'javax.annotation', name: 'javax.annotation-api', version: '1.2'
			    compile group: 'org.springframework', name: 'spring-web', version: '4.3.1.RELEASE'
			    compile group: 'org.javassist', name: 'javassist', version: '3.18.1-GA'
			    compile group: 'javax.ws.rs', name: 'javax.ws.rs-api', version: '2.0'
			    compile group: 'org.glassfish.hk2', name: 'spring-bridge', version: '2.5.0-b26', transitive:false
			    compile group: 'org.glassfish.jersey.ext', name: 'jersey-spring3', version: '2.7'
			                                                                                           
			    providedCompile group: 'javax.servlet', name: 'javax.servlet-api', version: '3.0.1'        
			    
			    testCompile group: 'junit', name: 'junit', version: '4.12'                                 
			    testCompile group: 'org.codenarc', name: 'CodeNarc', version: '0.27.0', transitive:false   
			    testCompile group: 'org.gmetrics', name: 'GMetrics', version: '0.7', transitive:false
			    testCompile group: 'org.apache.ant', name: 'ant', version: '1.8.4'                         
			    testCompile(group: 'org.eclipse.jetty.aggregate', name: 'jetty-all-server', version: '7.6.13.v20130916', transitive:false)
			    testCompile group: 'org.springframework', name: 'spring-test', version: '4.3.1.RELEASE'    
			    
			    warLibs group:'com.ibm', name:'somejar', version:'12.34.56'
			}
			 
			test {
			    include '**/SomeTestSuite.class'
			    jvmArgs '-Xmx1024m'
			}
			 
			war {
			    dependsOn createBuildVersionFile, createBuildTimestampFile
			   
			    baseName = BASE_NAME
			    classpath configurations.warLibs
			    classpath buildExtraDir
			}
			 
			pmd {
			    toolVersion = '4.2'
			    ruleSetFiles = files('Build/config/pmd/wystar-pmd-ruleset-strict.xml')   
			    ruleSets = []  // do not include default rulesets
			}
			 
			// Required for AHP builds (Build-Gradle.bat)
			task buildArtifacts(dependsOn: 'ear')
			 
			eclipse {
			    pathVariables 'GRADLE_IVY_REPO': file(System.getProperty("user.home") + '/.gradle/caches/modules-2/files-2.1')
			    // After you regenerate the Eclipse .classpath (gradle eclipseClasspath), then also:
			    //   * Remove groovy-all jar from the build path (because Eclipse includes its own Groovy support)
			    //   * Move the servlet-api:3.0.1 above GROOVY_SUPPORT in the build path.
			}

			task createVersionFile doLast {
			    buildExtraDir.mkdirs()
			    versionFile.text = "abc"
			}

			jar.dependsOn createVersionFile
			 
			javadoc {
			    title = 'MyProject'
			}
			 
			tasks.withType(FindBugs) {
			    reports {
			        xml.enabled = false
			        html.enabled = true
			    }
			}

			// Code Generation Scripts ---------------------------------
			 
			buildscript {
			    final CODE_GEN_PLUGIN_VERSION = '0.6'
			    repositories { ivy { url "http://some-nexus.acme.com:8081/nexus/content/repositories/releases/" } }
			    dependencies {
			        classpath group:'Other', name:'code-generation', version:CODE_GEN_PLUGIN_VERSION, transitive:false
			    }
			}
			apply plugin: 'some-code-generation'
			 
			// Cobertura -----------------------------------------------
			 
			buildscript {
			    repositories {
			        ivy {
			            url "http://some-nexus.acme.com:8081/nexus/content/repositories/thirdparty/"
			        }
			    }
			    dependencies {
			        classpath("net.saliman:gradle-cobertura-plugin:2.3.2")
			    }
			}
			apply plugin: 'cobertura'
			 
			cobertura {
			    coverageSourceDirs = sourceSets.main.groovy.srcDirs
			    coverageExcludes = ['.*Test.class', '.*test.*', '.*Stub.*', '.*Mock.*']
			    coverageFormats = ['html', 'xml']
			}
			configurations {
			    // https://github.com/stevesaliman/gradle-cobertura-plugin/issues/41
			    cobertura.exclude group: 'xalan'
			    cobertura.exclude group: 'xerces'
			}
		'''
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING).size() == 25
	}

	@Test
	void test_parse_UseOfGradleApi() {
		final SOURCE = """
			dependencies {
				compile "org.other:service:1.0:jdk15@jar"
			}
	
			boolean templateFileExists() {
			    FileCollection templateFiles = files(sourceSets.test.resources).filter { file -> file.name.toLowerCase().contains("jrxml") }
				return templateFiles
			}
		"""
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING).size() == 1
	}

	// Tests for other dependency options and formats
	
	@Test
	void test_parse_OtherProperties() {
		final SOURCE = """dependencies {
			compile group: 'org.h7', name: 'h7', version: '3.1', transitive:false, ext: 'jar', classifier:'jdk17', configuration:'c1'
			runtime "org.groovy:groovy:2.2.0@jar"
			compile "org.other:service:1.0:jdk15@jar"
		}"""
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [
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
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [
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
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [
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
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [
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
		assert parser.parse(APPLICATION_NAME, SOURCE, BINDING) == [
			new Dependency(applicationName:APPLICATION_NAME, configuration:"compile", group:"org.gradle", name:"api", version:"1.0"),
		]
	}
	
	// Tests for invalid DSL syntax/format
	
	@Test
	void test_parse_StringFormat_Empty() {
		shouldFail { parser.parse(APPLICATION_NAME, "dependencies { compile '' }", BINDING) }	
	}

	@Test
	void test_parse_IllegalGroovySyntax() {
		shouldFail(IllegalStateException) { parser.parse(APPLICATION_NAME, "%^@ &*[]", BINDING) }	
	}
	
}
