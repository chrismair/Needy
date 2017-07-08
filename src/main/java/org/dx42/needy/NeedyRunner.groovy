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
package org.dx42.needy

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.dx42.needy.parser.DependencyParser
import org.dx42.needy.parser.DependencyParserFactory
import org.dx42.needy.report.ReportWriter

class NeedyRunner {

	private static final Logger LOG = LoggerFactory.getLogger(NeedyRunner)
	
	NeedyConfiguration needyConfiguration
	DependencyParserFactory dependencyParserFactory = new DependencyParserFactory() 
	
	List<Dependency>  execute() {
		assert needyConfiguration
		
		def applicationBuilds = needyConfiguration.getApplicationBuilds()
		LOG.info("applicationBuilds=" + applicationBuilds)
		List<Dependency> allDependencies = []
		
		applicationBuilds.forEach{ ApplicationBuild applicationBuild ->
			LOG.info("Processing application [${applicationBuild.name}]")
			applicationBuild.buildScripts.each { BuildScript buildScript ->
				DependencyParser dependencyParser = dependencyParserFactory.getDependencyParser(applicationBuild.name, "gradle")
				String buildFileText = buildScript.getText()
				List<Dependency> buildFileDependencies = dependencyParser.parse(buildFileText)
				allDependencies.addAll(buildFileDependencies)
				
				buildFileDependencies.each { dependency ->
					LOG.info("  + $dependency")
				}
			}
		}
		
		needyConfiguration.reportWriters.each { ReportWriter reportWriter ->
			reportWriter.writeReport(allDependencies)
		}
		
		return allDependencies
	}
	
}
