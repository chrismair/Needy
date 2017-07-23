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
    compile 'org.dx42:needy:0.3' 
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

  Here is a sample "config.needy" DSL that parses multiple applications and generated two reports (text and HTML):
  
```groovy
needy {
        
    applications {
        Needy(url:"file:/home/workspaces/someproject/build.gradle")               // "Needy" application
        CodeNarc(url:"https://rawgit.com/CodeNarc/CodeNarc/master/build.gradle")  // "CodeNarc" application

        MyGrailsApp(url:"file:SampleGrails/grails-app/config/BuildConfig.groovy", type:"grails2")  // Grails 2.x "BuildConfig.groovy"
    }
    
    reports {
    	// Syntax:  <report-name>(<report-writer-class>) { <optional-report-writer-properties> }
    
        textReport("org.dx42.needy.report.ByArtifactTextReportWriter")    // Will write to stdout

        HtmlReport("org.dx42.needy.report.ByArtifactHtmlReportWriter") {
            outputFile = "Needy-Report.html"// Optional; If not set (null) then write report to stdout
            title = "My Sample Projects"	// Optional; report title
            includeApplications = null		// Optional; String value; comma-separated list of application names with optional wildcards ("*"); null mean include all
            excludeApplications = null		// Optional; String value; comma-separated list of application names with optional wildcards ("*"); null mean exclude none
        }
    }
}
```

### Application Configuration Properties
  - "url" -- the URL for the application build script file; required
  - "type" -- the type of build script: "gradle" (default) or "grails2"; optional
  - "properties" -- a Map of binding properties, used when evaluating the build script; optional
  - "description" -- a text description for the build script; for documentation only; optional
  - "componentId" -- an optional id to uniquely identify a build script when an application contains multiple build scripts; optional

### Allowed Types:
  - "gradle" -- a Gradle "build.gradle" file. This is the default type, if no type is specified. 
  -  "grails2" -- a Grails 2.x "BuildConfig.groovy" file.

### Reports

  The provided report types are:
  - `org.dx42.needy.report.ByArtifactTextReportWriter` - Simple text report listing all artifacts
  - `org.dx42.needy.report.ByArtifactHtmlReportWriter` - HTML report listing all artifacts; See [Sample HTML Report](http://htmlpreview.github.com/?https://github.com/dx42/Needy/blob/master/samples/sample-html-report.html)

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
  - SLF4J API and implementation jar(s) -- e.g. Logback
  - TBD...
  
## Road Map

  - Parse Maven POM XML
  - XML Report and/or JSON Report
  - Checker/Handler/Visitor way to analyze or validate each dependency -- check for known vulnerabilities or disallow artifacts by policy
