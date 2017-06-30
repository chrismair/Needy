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

class ByArtifactTextReportWriter implements ReportWriter {

	String outputFile
	
	@Override
	void writeReport(List<Dependency> dependencies) {
		assert dependencies != null
	
		Map sortedMap = ReportUtil.buildMapOfArtifactNameToApplicationNames(dependencies)
		
		def printWriter = createPrintWriter()
		
		printWriter.withWriter { w -> 
			w.println "Needy\n"
			sortedMap.each { k, v ->
				w.println(/"$k" -- $v/)
			}
		}
	}
	
	private PrintWriter createPrintWriter() {
		if (outputFile) {
			def file = new File(outputFile)
			return file.newPrintWriter()
		}
		return System.out.newPrintWriter()
	}
	
}
