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
package needy

import java.util.List

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NeedyRunner {

	private static final Logger LOG = LoggerFactory.getLogger(NeedyRunner)
	
	ApplicationBuildSet applicationBuildSet
	
	void execute() {
		assert applicationBuildSet
		
		def applicationBuilds = applicationBuildSet.getApplicationBuilds()
		LOG.info("applicationBuilds=" + applicationBuilds)
		applicationBuilds.forEach{ ApplicationBuild applicationBuild ->
			LOG.info("Processing application [${applicationBuild.name}]")
			DependencyParser dependencyParser = new GroovyDslGradleDependencyParser(applicationBuild.name)
			applicationBuild.buildScripts.each { BuildScript buildScript ->
				String buildFileText = buildScript.getText()
				List<Dependency> dependencies = dependencyParser.parse(buildFileText)
				LOG.info("    dependencies=$dependencies")
			}
		}

	}
	
}
