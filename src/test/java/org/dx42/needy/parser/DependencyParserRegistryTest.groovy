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
 * Tests for DependencyParserRegistry
 *
 * @author Chris Mair
 */
class DependencyParserRegistryTest extends AbstractTestCase {

    private static final String NAME = "abc"
    private static final DependencyParser DEPENDENCY_PARSER = new GrailsBuildConfigDependencyParser()

    private DependencyParserRegistry registry = new DependencyParserRegistry()

    @Test
    void test_getDependencyParser_nullOrEmpty_ReturnsDefault() {
        assert registry.getDependencyParser(null) instanceof GradleDependencyParser
        assert registry.getDependencyParser("") instanceof GradleDependencyParser
    }

    @Test
    void test_getDependencyParser_default() {
        assert registry.getDependencyParser("default") instanceof GradleDependencyParser
        assert registry.getDependencyParser("DeFAulT") instanceof GradleDependencyParser
    }

    @Test
    void test_getDependencyParser_null_ReturnsDefault() {
        assert registry.getDependencyParser(null) instanceof GradleDependencyParser
    }

    @Test
    void test_getDependencyParser_gradle() {
        assert registry.getDependencyParser("gradle") instanceof GradleDependencyParser
        assert registry.getDependencyParser("GRadLe") instanceof GradleDependencyParser
    }

    @Test
    void test_getDependencyParser_grails2() {
        assert registry.getDependencyParser("grails2") instanceof GrailsBuildConfigDependencyParser
        assert registry.getDependencyParser("gRAIls2") instanceof GrailsBuildConfigDependencyParser
    }

    @Test
    void test_getDependencyParser_ReturnsRegisteredType() {
        registry.registerDependencyParser("aBc", DEPENDENCY_PARSER)
        assert registry.getDependencyParser("abc") == DEPENDENCY_PARSER
        assert registry.getDependencyParser("AbC") == DEPENDENCY_PARSER
    }

    @Test
    void test_getDependencyParser_UnknownType() {
        shouldFailWithMessage("unknown") { registry.getDependencyParser("unknown") }
    }

    @Test
    void test_registerDependencyParser_OverrideDefault() {
        registry.registerDependencyParser("defAUlt", DEPENDENCY_PARSER)
        assert registry.getDependencyParser("default") == DEPENDENCY_PARSER
    }

    @Test
    void test_registerDependencyParser_NullDependencyParser() {
        shouldFailWithMessage("dependencyParser") { registry.registerDependencyParser(NAME, null) }
    }

    @Test
    void test_registerDependencyParser_NullOrEmptyType() {
        shouldFailWithMessage("type") { registry.registerDependencyParser(null, DEPENDENCY_PARSER) }
        shouldFailWithMessage("type") { registry.registerDependencyParser("", DEPENDENCY_PARSER) }
    }

}
