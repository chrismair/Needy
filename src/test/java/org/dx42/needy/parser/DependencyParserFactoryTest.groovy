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

package org.dx42.needy.parser

import org.dx42.needy.AbstractTestCase
import org.junit.Test

/**
 * Tests for DependencyParserFactory
 *
 * @author Chris Mair
 */
class DependencyParserFactoryTest extends AbstractTestCase {

	private static final String NAME = "App1"
	
	private DependencyParserFactory factory = new DependencyParserFactory()
	
	@Test
	void test_getDependencyParser_NullType_ReturnsDefault() {
		def dependencyParser = factory.getDependencyParser(NAME, null)
		assert dependencyParser instanceof GradleDependencyParser
		assert dependencyParser.getApplicationName() == NAME
	}
	
	@Test
	void test_getDependencyParser_EmptyType_ReturnsDefault() {
		def dependencyParser = factory.getDependencyParser(NAME, "")
		assert dependencyParser instanceof GradleDependencyParser
		assert dependencyParser.getApplicationName() == NAME
	}
	
	@Test
	void test_getDependencyParser_gradle() {
		def dependencyParser = factory.getDependencyParser(NAME, "gradle")
		assert dependencyParser instanceof GradleDependencyParser
		assert dependencyParser.getApplicationName() == NAME
	}
	
	@Test
	void test_getDependencyParser_gradle_CaseInsensitive() {
		def dependencyParser = factory.getDependencyParser(NAME, "GRadLe")
		assert dependencyParser instanceof GradleDependencyParser
		assert dependencyParser.getApplicationName() == NAME
	}
	
	@Test
	void test_getDependencyParser_UnknownType() {
		shouldFailWithMessage(IllegalArgumentException, "unknown") { factory.getDependencyParser(NAME, "unknown") }
	}
	
}
