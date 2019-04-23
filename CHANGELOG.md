# Needy Change Log


Version 0.17 (April 23, 2019)
--------------------------------------------------
- MavenCentralArtifactLatestVersionService: Add support for "needy.maven-central.delay.milliseconds" system property.


Version 0.16 (April 21, 2019)
--------------------------------------------------
- #11: New **ArtifactLatestVersionHtmlReport**: Report listing latest version available for each artifact.
- #9: Report: Include/exclude configuration (scope); *includeConfigurationNames* and *excludeConfigurationNames* properties.
- #12: Upgrade to CodeNarc v1.3. Enable new rules.
- Upgrade to Groovy 2.4.13


Version 0.15 (Dec 11, 2018)
--------------------------------------------------
- #10: MissingMethodException: No signature of method: org.dx42.needy.parser.GradleDependencyParser.classpath()


Version 0.14 (Feb 17, 2018)
--------------------------------------------------
- #8: Fix Error: GroovyCastException: Cannot cast Closure to Map.
- #7: Upgrade to CodeNarc 1.1. Enable new rules: Indentation., MethodReturnTypeRequired, InvertedCondition, MethodParameterTypeRequired, FieldTypeRequired, BlockStartsWithBlankLine, BlockEndsWithBlankLine., NoTabCharacter. Switch to explicit list of rules rather than using legacy rulesets. Also: ConsecutiveBlankLines, MissingBlankLineAfterImports, MissingBlankLineAfterPackage.


Version 0.13 (Dec 2, 2017)
--------------------------------------------------
- #5: **GradleDependencyParser**: Add org.gradle.api.tasks.* to standard Gradle imports.


Version 0.12 (Nov 29, 2017)
--------------------------------------------------
- #4: GradleDependencyParser: Add org.gradle.api.* to STANDARD_GRADLE_API_IMPORTS.


Version 0.11 (Nov 19, 2017)
--------------------------------------------------
- #1: **Needy**: Pass in “config.needy” file/path as optional command-line arg.
- #2: **GradleDependencyParser**: Also include dependencies within the `buildscript { }` block.


Version 0.10 (Sep 17, 2017)
--------------------------------------------------
- `GradleDependencyParser`: Add entries for `file()` and `fileTree()` dependencies. Add *includeFileDependencies* property.
- Upgrade to CodeNarc 1.0 and GMetrics 1.0.


Version 0.9 (Aug 20, 2017)
--------------------------------------------------
- `ByArtifactHtmlReport` and `ArtifactsWithMultipleVersionsHtmlReport`: Add "Applications" section to HTML reports that lists filtered list of application names.
- `ByArtifactHtmlReport` and `ArtifactsWithMultipleVersionsHtmlReport`: Add support for *includeArtifacts* and *excludeArtifacts* properties.


Version 0.8 (Jul 30, 2017)
--------------------------------------------------
- [BREAKING CHANGE] Rename XxxReportWriter classes to XxxReport.
- `ByArtifactHtmlReportWriter`: When filter out applications, reset numbering.
- `ByArtifactHtmlReport`: Add *notesHtml* property to optionally embed custom HTML at top of report.
- New `ArtifactsWithMultipleVersionsHtmlReport`.


Version 0.7 (Jul 23, 2017)
--------------------------------------------------
- `ByArtifactHtmlReportWriter`: Support *includeApplications* and *excludeApplications* filters.
- New `WildcardUtil`.
- `AbstractReportWriter`: Add *includeApplications* and *excludeApplications* properties and `includeApplication(String applicationName)` and `excludeApplication(String applicationName)`.
- New `DependencyParserRegistry`. Change `NeedyRunner` to use it.
- `GrailsBuildConfigDependencyParser`: Change *includePlugins* default to true.
- Move `DoNothing` into standalone top-level class file.
- Include sample HTML report in distribution and reference from README.


Version 0.6 (Jul 19, 2017)
--------------------------------------------------
- `GrailsBuildConfigDependencyParser`: Include dependencies defined with `plugins { }` closure. Support *includePlugins* property.
- Config DSL: Change report to specify report name, e.g., `textReport("org.dx42.needy.report.ByArtifactTextReportWriter")`.


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

<https://github.com/dx42/Needy>
