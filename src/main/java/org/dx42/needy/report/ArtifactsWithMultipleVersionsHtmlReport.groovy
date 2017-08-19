
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

import org.dx42.needy.Artifact
import org.dx42.needy.Dependency

/**
 * HTML Report that displays only dependencies for artifacts with more than one version used 
 * across the applications, sorted by artifact name. 
 *
 * @author Chris Mair
 */
class ArtifactsWithMultipleVersionsHtmlReport extends AbstractHtmlReport {

	protected buildBodySection(List<Dependency> dependencies) {
		Map sortedMap = buildArtifactMap(dependencies)

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

	private buildDependencyTable(Map sortedMap) {
		return {
			div(class: 'summary') {
				h2("Artifacts with Multiple Versions")
				table {
					tr(class:'tableHeader') {
						th("#")
						th("Group")
						th("Name")
						th("Version")
						th("Applications")
					}
					int index = 1
					sortedMap.each{ k, v ->
						def closure = buildDependencyRow(k, v, index)
						if (closure) {
							out << closure
							index++
						}
					} 
				}
			}
		}
	}

	private SortedMap<Artifact, Set<String>> buildArtifactMap(List<Dependency> dependencies) {
		SortedMap<Artifact, Set<String>> map = new TreeMap<>(ARTIFACT_COMPARATOR)
		
		dependencies.each { dependency ->
			Artifact key = dependency.artifact
			
			if (containsArtifactWithDifferentVersion(dependencies, dependency)) {
				if (!map.containsKey(key)) {
					map[key] = new TreeSet<String>()
				}
				map[key] << dependency.applicationName
			}
		}
		return map
	}

	private boolean containsArtifactWithDifferentVersion(List<Dependency> dependencies, Dependency dependency) {
		Artifact artifact = dependency.artifact
		return dependencies.find { dep ->  			
			Artifact otherArtifact = dep.artifact
			
			// If any other dependency artifact has the same group and name, but different version
			return isIncludedApplication(dep.applicationName) && 
				artifact.group == otherArtifact.group && artifact.name == otherArtifact.name && artifact.version != otherArtifact.version
		}
	}

}
