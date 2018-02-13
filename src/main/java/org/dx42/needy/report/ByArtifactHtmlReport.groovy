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

import static org.dx42.needy.report.ReportUtil.*

import org.dx42.needy.Dependency

/**
 * HTML Report that displays all dependencies sorted by artifact name.
 *
 * @author Chris Mair
 */
class ByArtifactHtmlReport extends AbstractHtmlReport {

    @Override
    protected Closure buildBodySection(List<Dependency> dependencies) {
        Map sortedMap = ReportUtil.buildMapOfArtifactToApplicationNames(dependencies)

        return {
            body {
                h1(STANDARD_TITLE)
                out << buildReportMetadata()
                if (notesHtml) {
                    unescaped << notesHtml
                }
                out << buildDependencyTable(sortedMap)
                out << buildApplicationList(dependencies)
            }
        }
    }

    private Closure buildDependencyTable(Map sortedMap) {
        return {
            div(class:'summary') {
                h2("Dependencies")
                table {
                    tr(class:'tableHeader') {
                        th("#")
                        th("Group")
                        th("Name")
                        th("Version")
                        th("Applications")
                    }
                    int index = 1
                    sortedMap.each { artifact, names ->
                        if (isIncludedArtifact(artifact)) {
                            def closure = buildDependencyRow(artifact, names, index)
                            if (closure) {
                                out << closure
                                index++
                            }
                        }
                    }
                }
            }
        }
    }

}
