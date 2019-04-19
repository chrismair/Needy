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

import org.dx42.needy.Dependency
import org.dx42.needy.service.ArtifactLatestVersionService
import org.dx42.needy.service.MavenCentralArtifactLatestVersionService

import groovy.transform.Immutable

class ArtifactLatestVersionHtmlReport extends AbstractHtmlReport {

    protected static final String TITLE = 'Latest Artifact Version'

    @Immutable
    private static class GroupAndName {

        String group
        String name

        @Override
        String toString() {
            return group + ":" + name
        }
    }

    protected ArtifactLatestVersionService artifactLatestVersionService = new MavenCentralArtifactLatestVersionService()

    private Map<GroupAndName, String> buildLatestVersionMap(List<Dependency> dependencies) {
        def map = [:]
        dependencies.each { dep ->
            def groupAndName = new GroupAndName(group:dep.group, name:dep.name)
            map[groupAndName] = artifactLatestVersionService.getLatestVersion(dep.group, dep.name)
        }
        return map.sort { e1, e2 -> e1.key.toString() <=> e2.key.toString() }
    }

    @Override
    protected Closure buildBodySection(List<Dependency> dependencies) {
        Map<GroupAndName, String> latestVersionMap = buildLatestVersionMap(dependencies)

        return {
            body {
                h1(TITLE)
                out << buildReportMetadata()
                if (notesHtml) {
                    unescaped << notesHtml
                }
                out << buildLatestVersionTable(latestVersionMap)
            }
        }
    }

    private Closure buildLatestVersionTable(Map latestVersionMap) {
        return {
            div(class:'summary') {
                h2("Artifacts")
                table {
                    tr(class:'tableHeader') {
                        th("#")
                        th("Group")
                        th("Name")
                        th("Latest Version")
                    }
                    int index = 1
                    latestVersionMap.each { groupAndName, latestVersion ->
                        out << buildLatestVersionRow(groupAndName, latestVersion, index)
                        index++
                    }
                }
            }
        }
    }

    protected Closure buildLatestVersionRow(GroupAndName groupAndName, String latestVersion, int index) {
        return {
            tr {
                td(index)
                td(groupAndName.group)
                td(groupAndName.name)
                td(latestVersion)
            }
        }
    }

}