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
 * Tests for AbstractReportWriter
 * 
 * @author Chris Mair
 */
class AbstractReportWriterTest extends AbstractTestCase {

	private static final Date DATE = new Date(1499477419708L)
	private static final List<Dependency> DEPENDENCIES = [
		new Dependency(applicationName:"App1", group:"acme", name:"service", version:"2.0")]

	private Writer writer
	private List<Dependency> dependencies
	private reportWriter = new AbstractReportWriter() {  
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
		reportWriter.writeReport(DEPENDENCIES)
		assert dependencies == DEPENDENCIES
		assert writer instanceof PrintWriter
	}

	@Test
	void test_writeReport_CreateParentDirectoriesIfNecessary() {
		def parentDir = "src/test/resources/doesNotExist"
		def reportFile = parentDir + "/tmpfile.txt"
		reportWriter.outputFile = reportFile
		new File(parentDir).deleteOnExit()
		new File(reportFile).deleteOnExit()
		reportWriter.writeReport(DEPENDENCIES)
		assert dependencies == DEPENDENCIES
	}

	@Test
	void test_writeReport_NullDependencies() {
		shouldFailWithMessage('dependencies') { reportWriter.writeReport(null) }
	}

	@Test
	void test_getFormattedTimestamp() {
		reportWriter.getDate = { DATE }
		log(reportWriter.getFormattedTimestamp())
		assert reportWriter.getFormattedTimestamp() == java.text.DateFormat.getDateTimeInstance().format(DATE)
	}
	
	@Test
	void test_createPrintWriter() {
		def writer = reportWriter.createPrintWriter()
		assert writer
	}
	
	@Test
	void test_createPrintWriter_outputFile() {
		reportWriter.outputFile = "build/tmpfile.txt"
		new File(reportWriter.outputFile).deleteOnExit()
		def writer = reportWriter.createPrintWriter()
		assert writer
	}
	
	@Test
	void test_getDate() {
		assert reportWriter.getDate() instanceof Date
	}
	
	@Test
	void test_includeApplication() {
		assert reportWriter.includeApplication("a")

		reportWriter.includeApplications = "A*"		
		assert reportWriter.includeApplication("AAA")
		assert reportWriter.includeApplication("A-B")
		assert !reportWriter.includeApplication("B")
		
		reportWriter.includeApplications = "A, B*"		
		assert reportWriter.includeApplication("A")
		assert reportWriter.includeApplication("B")
		assert reportWriter.includeApplication("BAB")
		assert !reportWriter.includeApplication("A-B")
		assert !reportWriter.includeApplication("AB")
	}
	
	@Test
	void test_excludeApplication() {
		assert !reportWriter.excludeApplication("a")
		
		reportWriter.excludeApplications = "A*"
		assert reportWriter.excludeApplication("AAA")
		assert reportWriter.excludeApplication("A-B")
		assert !reportWriter.excludeApplication("B")
		
		reportWriter.excludeApplications = "A, B*"
		assert reportWriter.excludeApplication("A")
		assert reportWriter.excludeApplication("B")
		assert reportWriter.excludeApplication("BAB")
		assert !reportWriter.excludeApplication("A-B")
		assert !reportWriter.excludeApplication("AB")
	}
	
}
