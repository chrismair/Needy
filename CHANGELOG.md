# Needy Change Log
<https://github.com/dx42/Needy>


Version 0.5 (Jul 16, 2017)
--------------------------------------------------
- `DslNeedyConfiguration`: Fix not picking up "type" from: Application(Map1, Map2).
- `build.gradle`: Switch to explicit and more fine-grained dependencies. Fix "Class path contains multiple SLF4J bindings" warning.
- `DependencyParser`: Change to `parse(String applicationName, String source, Map binding)`.
- `NeedyRunner`, `DslNeedyConfiguration`, `GradleDependencyParser` and `GrailsBuildConfigDependencyParser`: Support passed-in binding.
- Convert CHANGELOG to Markdown.


Version 0.4 (Jul 13, 2017)
--------------------------------------------------
- New `GrailsBuildConfigDependencyParser`: DependencyParser for Grails 2.x "BuildConfig.groovy" files. Type="grails2"
- NeedyRunner: use new `DependencyParserFactory`.
- New `DependencyParserFactory` with `getDependencyParser(String name, String type)`. Use in `NeedyRunner`.
- `ByArtifactHtmlReportWriter`: Add <meta http-equiv="Content-Type" content="text/html">; Make report title bigger
- `AbstractReportWriter`: Create parent directories if necessary.
- `BuildScript`: add `getType()`, `getDescription()` and `getComponentId()` methods to interface
- Change `DependencyParser` method to `parse(String applicationName, String source)`.
- New `ParseUtil`. Use in DependencyParser classes.
- `DslNeedyConfiguration`: Parse build script type


Version 0.3 (Jul 6, 2017)
--------------------------------------------------
- Change package to `org.dx42.needy`.
- Publish to JCenter; group = org.dx42.
- Introduce parser sub-package.
- `DslNeedyConfiguration`: Remove support for applicationName(String) and applicationName(List<String>).
