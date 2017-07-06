# Needy  (https://github.com/dx42/Needy)

**Needy** Produces simple consolidated dependency report across multiple Gradle projects.

## Running

### Run as a Gradle Task

  Here is a sample Gradle script to run Needy. It assumes that a "config.needy" is in the current directory:
  
```
	apply plugin: 'java'
	 
	repositories {
	    jcenter()
	}
	 
	dependencies {
	    compile 'org.dx42:needy:0.2' 
	    compile 'ch.qos.logback:logback-classic:1.2.3'
	}
	 
	task execute(type:JavaExec) {
	    main = "org.dx42.needy.Needy"
	    classpath = sourceSets.main.runtimeClasspath
	}
```

### Run as a Linux Script

  Here is a sample Linux script to execute Needy, assuming that a "config.needy" and the required jars are in the current directory:
  
```
 	needyJar="./Needy.jar"
	groovyJar="./groovy-all-2.4.6.jar"
	logbackJar="./logback-classic-1.2.3.jar"
	logbackCoreJar="./logback-core-1.2.3.jar"
	slf4jJar="./slf4j-api-1.7.25.jar"
	classpath="$needyJar:$groovyJar:$logbackJar:$logbackCoreJar:$slf4jJar"
	java  -classpath $classpath org.dx42.needy.Needy
```


## Configuration

  Here is a sample "config.needy" DSL that pulls in two applications and generated two reports (text and HTML):
  
```groovy
	needy {
		
		applications {
			Needy(url:"file:/home/workspaces/someproject/build.gradle")					// "Needy" application
			CodeNarc(url:"https://rawgit.com/CodeNarc/CodeNarc/master/build.gradle")	// "CodeNarc" application
		}
	
		reports {
			report("org.dx42.needy.report.ByArtifactTextReportWriter") { }		// Text report; will write to stdout
			
			report("org.dx42.needy.report.ByArtifactHtmlReportWriter") {		// HTML report
				outputFile = "Needy-Report.html"
				title = "My Sample Projects"
			}
		}
	}
```

## Known Limitations

  - This implementation is quite limited. 
  - It supports only relatively standard Gradle syntax.
  - It only handles declared dependencies  (not transitive dependencies).
  - The report does not include dependencies (jars) embedded within the project
  - When running Needy, you must include jars on the classpath for all classes referenced within the build files being parsed, e.g. the Gradle API or application-specific classes.
  - TBD... 
  

## Needy Runtime Dependencies

  - "config.needy" configuration file in the current directory
  - Groovy jar
  - Logback jar(s)
  - SLF4J jar
  - TBD...