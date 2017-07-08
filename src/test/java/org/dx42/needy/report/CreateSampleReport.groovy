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
	
	private static final String OUTPUT_FILE = "build/reports/sample-html-report.html"
	private static final List<Dependency> DEPENDENCIES = [
		new Dependency(applicationName:"Third", group:"org.other", name:"service", version:"2.0"),
		new Dependency(applicationName:"Sample1", group:"org.hibernate", name:"hibernate-core", version:"3.1"),
		new Dependency(applicationName:"Sample_Two", group:"log4j", name:"log4j", version:"1.2.14"),
		new Dependency(applicationName:"Sample1", group:"log4j", name:"log4j", version:"1.2.14"),
		new Dependency(applicationName:"Third", group:"log4j", name:"log4j", version:"1.2.14"),
		new Dependency(applicationName:"Third", group:"log4j-extra", name:"stuff", version:"1.0"),
	]

	static void main(String[] args) {
		def reportWriter = new ByArtifactHtmlReportWriter()
		reportWriter.outputFile = OUTPUT_FILE
		reportWriter.title = "My Sample Project"
		
		// TODO Remove once it auto-creates the parent dir
		new File(OUTPUT_FILE).getParentFile()?.mkdirs()
		
		reportWriter.writeReport(DEPENDENCIES)
	}
	
}
