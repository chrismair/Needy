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
package dx42.needy.report

import dx42.needy.Dependency

abstract class AbstractReportWriter implements ReportWriter {

	String outputFile
	
	protected getFormattedTimestamp = {
		def dateFormat = java.text.DateFormat.getDateTimeInstance()
		dateFormat.format(new Date())
	}
	
	abstract void writeReport(Writer writer, List<Dependency> dependencies) 
	
	@Override
	void writeReport(List<Dependency> dependencies) {
		assert dependencies != null
	
		def printWriter = createPrintWriter()
		writeReport(printWriter, dependencies)
	}
	
	protected PrintWriter createPrintWriter() {
		if (outputFile) {
			def file = new File(outputFile)
			return file.newPrintWriter()
		}
		return System.out.newPrintWriter()
	}
	
}
