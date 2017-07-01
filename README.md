# Needy  (https://github.com/dx42/Needy)

**Needy** Produces simple consolidated dependency report across multiple Gradle projects.

## Running

### Run as a Linux Script

  Here is a sample Linux script to execute Needy, assuming that a "config.needy" and the required jars are in the current directory:
  
```
 	needyJar="./Needy.jar"
	groovyJar="./groovy-all-2.4.6.jar"
	logbackJar="./logback-classic-1.2.3.jar"
	logbackCoreJar="./logback-core-1.2.3.jar"
	slf4jJar="./slf4j-api-1.7.25.jar"
	classpath="$needyJar:$groovyJar:$logbackJar:$logbackCoreJar:$slf4jJar"
	java  -classpath $classpath dx42.needy.Needy
```

### Run as a Gradle Task

  TBD


## Configuration

  Here is a sample "config.needy" DSL that pulls in two applications and generated two reports (text and HTML):
  
```groovy
	needy {
		
		applications {
			Needy("file:/home/workspaces/someproject/build.gradle")
			CodeNarc("https://rawgit.com/CodeNarc/CodeNarc/master/build.gradle")
		}
	
		reports {
			report("dx42.needy.report.ByArtifactTextReportWriter") { }
			
			report("dx42.needy.report.ByArtifactHtmlReportWriter") {
				outputFile = "Needy-Report.html"
			}
		}
	}
```

## Known Limitations

  - This implementation is quite limited. 
  - It supports only relatively standard Gradle syntax.
  - It only handles declared dependencies  (not transitive dependencies).
  - TBD... 
  

## Needy Runtime Dependencies

  - "config.needy" configuration file in the current directory
  - Groovy jar
  - Logback jar(s)
  - SLF4J jar
  - TBD...