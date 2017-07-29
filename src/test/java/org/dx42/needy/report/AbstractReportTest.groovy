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

import org.dx42.needy.AbstractTestCase
import org.dx42.needy.Dependency
import org.junit.Test

/**
 * Tests for AbstractReport
 * 
 * @author Chris Mair
 */
class AbstractReportTest extends AbstractTestCase {

	private static final Date DATE = new Date(1499477419708L)
	private static final List<Dependency> DEPENDENCIES = [
		new Dependency(applicationName:"App1", group:"acme", name:"service", version:"2.0")]

	private Writer writer
	private List<Dependency> dependencies
	private report = new AbstractReport() {  
		@Override
		void writeReport(Writer writer, List<Dependency> dependencies) {
			this.writer = writer
			this.dependencies = dependencies
		}
	}
	
	//--------------------------------------------------------------------------
	// Tests
	//--------------------------------------------------------------------------
	
	@Test
	void test_writeReport() {
		report.writeReport(DEPENDENCIES)
		assert dependencies == DEPENDENCIES
		assert writer instanceof PrintWriter
	}

	@Test
	void test_writeReport_CreateParentDirectoriesIfNecessary() {
		def parentDir = "src/test/resources/doesNotExist"
		def reportFile = parentDir + "/tmpfile.txt"
		report.outputFile = reportFile
		new File(parentDir).deleteOnExit()
		new File(reportFile).deleteOnExit()
		report.writeReport(DEPENDENCIES)
		assert dependencies == DEPENDENCIES
	}

	@Test
	void test_writeReport_NullDependencies() {
		shouldFailWithMessage('dependencies') { report.writeReport(null) }
	}

	@Test
	void test_getFormattedTimestamp() {
		report.getDate = { DATE }
		log(report.getFormattedTimestamp())
		assert report.getFormattedTimestamp() == java.text.DateFormat.getDateTimeInstance().format(DATE)
	}
	
	@Test
	void test_createPrintWriter() {
		def writer = report.createPrintWriter()
		assert writer
	}
	
	@Test
	void test_createPrintWriter_outputFile() {
		report.outputFile = "build/tmpfile.txt"
		new File(report.outputFile).deleteOnExit()
		def writer = report.createPrintWriter()
		assert writer
	}
	
	@Test
	void test_getDate() {
		assert report.getDate() instanceof Date
	}
	
	@Test
	void test_includeApplication() {
		assert report.includeApplication("a")

		report.includeApplications = "A*"		
		assert report.includeApplication("AAA")
		assert report.includeApplication("A-B")
		assert !report.includeApplication("B")
		
		report.includeApplications = "A, B*"		
		assert report.includeApplication("A")
		assert report.includeApplication("B")
		assert report.includeApplication("BAB")
		assert !report.includeApplication("A-B")
		assert !report.includeApplication("AB")
	}
	
	@Test
	void test_excludeApplication() {
		assert !report.excludeApplication("a")
		
		report.excludeApplications = "A*"
		assert report.excludeApplication("AAA")
		assert report.excludeApplication("A-B")
		assert !report.excludeApplication("B")
		
		report.excludeApplications = "A, B*"
		assert report.excludeApplication("A")
		assert report.excludeApplication("B")
		assert report.excludeApplication("BAB")
		assert !report.excludeApplication("A-B")
		assert !report.excludeApplication("AB")
	}
	
}
