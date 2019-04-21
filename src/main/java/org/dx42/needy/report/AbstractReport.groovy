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
package org.dx42.needy.report

import org.dx42.needy.Artifact
import org.dx42.needy.Dependency
import org.dx42.needy.util.WildcardUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractReport implements Report {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractReport)

    String outputFile
    String includeApplications
    String excludeApplications
    String includeArtifacts
    String excludeArtifacts
    String includeConfigurationNames
    String excludeConfigurationNames

    protected Closure getDate = { new Date() }

    abstract void writeReport(Writer writer, List<Dependency> dependencies)

    @Override
    void writeReport(List<Dependency> dependencies) {
        assert dependencies != null

        def printWriter = createPrintWriter()
        def includedDependencies = dependencies.findAll { dep -> isIncludedConfiguration(dep.configuration) }
        writeReport(printWriter, includedDependencies)

        if (outputFile) {
            LOG.info("Report written to [$outputFile]")
        }
    }

    protected Closure getFormattedTimestamp = {
        def dateFormat = java.text.DateFormat.getDateTimeInstance()
        dateFormat.format(getDate())
    }

    protected PrintWriter createPrintWriter() {
        if (outputFile) {
            new File(outputFile).getParentFile()?.mkdirs()
            def file = new File(outputFile)
            return file.newPrintWriter()
        }
        return System.out.newPrintWriter()
    }

    protected boolean matchesIncludeApplications(String applicationName) {
        return includeApplications ? WildcardUtil.matches(applicationName, includeApplications) : true
    }

    protected boolean matchesExcludeApplications(String applicationName) {
        return excludeApplications ? WildcardUtil.matches(applicationName, excludeApplications) : false
    }

    protected boolean isIncludedApplication(String applicationName) {
        return matchesIncludeApplications(applicationName) && !matchesExcludeApplications(applicationName)
    }

    protected boolean matchesIncludeArtifacts(Artifact artifact) {
        return includeArtifacts ? WildcardUtil.matches(artifact.toString(), includeArtifacts) : true
    }

    protected boolean matchesExcludeArtifacts(Artifact artifact) {
        return excludeArtifacts ? WildcardUtil.matches(artifact.toString(), excludeArtifacts) : false
    }

    protected boolean isIncludedArtifact(Artifact artifact) {
        return matchesIncludeArtifacts(artifact) && !matchesExcludeArtifacts(artifact)
    }

    protected boolean matchesIncludeConfigurationNames(String configurationName) {
        return includeConfigurationNames ? WildcardUtil.matches(configurationName, includeConfigurationNames) : true
    }

    protected boolean matchesExcludeConfigurationNames(String configurationName) {
        return excludeConfigurationNames ? WildcardUtil.matches(configurationName, excludeConfigurationNames) : false
    }

    protected boolean isIncludedConfiguration(String configurationName) {
        return matchesIncludeConfigurationNames(configurationName) && !matchesExcludeConfigurationNames(configurationName)
    }

    protected SortedSet<String> getApplicationNames(List<Dependency> dependencies) {
        SortedSet<String> names = [] as SortedSet
        dependencies.each { dependency ->
            String name = dependency.applicationName
            if (isIncludedApplication(name)) {
                names << name
            }
        }
        return names
    }

}
