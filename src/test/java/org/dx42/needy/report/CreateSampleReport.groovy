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

/**
 * Java application to create a sample Needy HTML report
 * 
 * @author Chris Mair
 */
class CreateSampleReport {
	
	private static final String OUTPUT_FILE = "samples/sample-html-report.html"
	private static final List<Dependency> DEPENDENCIES = [
		new Dependency(applicationName:"Sample1", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
		new Dependency(applicationName:"Sample1", group:"log4j", name:"log4j", version:"1.2.14"),
		new Dependency(applicationName:"Sample1", group:"org.slf4j", name:"slf4j-api", version:"1.7.25"),
		new Dependency(applicationName:"Sample_Two", group:"log4j", name:"log4j", version:"1.2.14"),
		new Dependency(applicationName:"Sample_Two", group:"commons-codec", name:"commons-codec", version:"1.6"),
		new Dependency(applicationName:"Sample_Two", group:"junit", name:"junit", version:"4.12"),
		new Dependency(applicationName:"Sample_Two", group:"com.google.guava", name:"guava", version:"14.0.1"),
		new Dependency(applicationName:"MyApp3", group:"org.other", name:"service", version:"2.0"),
		new Dependency(applicationName:"MyApp3", group:"log4j", name:"log4j", version:"1.2.14"),
		new Dependency(applicationName:"MyApp3", group:"log4j-extra", name:"stuff", version:"1.0"),
		new Dependency(applicationName:"MyApp3", group:"commons-codec", name:"commons-codec", version:"1.6"),
		new Dependency(applicationName:"MyApp3", group:"org.codehaus.groovy", name:"groovy-all", version:"2.3.9"),
		new Dependency(applicationName:"MyApp3", group:"org.slf4j", name:"slf4j-api", version:"1.7.25"),
	]

	static void main(String[] args) {
		def report = new ByArtifactHtmlReport()
		report.outputFile = OUTPUT_FILE
		report.title = "Sample Project"
		report.writeReport(DEPENDENCIES)
	}
	
}
