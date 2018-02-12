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
import org.dx42.needy.Dependency
import org.junit.Test

/**
 * Tests for ParseUtil
 *
 * @author Chris Mair
 */
class ParseUtilTest extends AbstractTestCase {

    private static final String APPLICATION_NAME = "MyApp1"
    private static final String CONFIGURATION_NAME = "testCompile"

    @Test
    void test_createDependencyFromString() {
        assert fromString("org.hibernate:hibernate-core:3.1") == dependency(group:"org.hibernate", name:"hibernate-core", version:"3.1")
        assert fromString("org.hibernate:hibernate-core:3.1") == dependency(group:"org.hibernate", name:"hibernate-core", version:"3.1")
    }
    
    @Test
    void test_createDependencyFromString_NameAndVersionOnly() {
        assert fromString("MyUtil:9") == dependency(group:null, name:"MyUtil", version:"9")
    }

    @Test
    void test_createDependencyFromString_NameOnly() {
        assert fromString("MyUtil") == dependency(group:null, name:"MyUtil", version:null)
    }

    @Test
    void test_createDependencyFromString_IncludesArtifactType() {
        assert fromString("org.groovy:groovy:2.2.0@jar") == dependency(group:"org.groovy", name:"groovy", version:"2.2.0")
        // TODO Is this the correct behavior?
        assert fromString("org.other:service:1.0:jdk15@jar") == dependency(group:"org.other", name:"service", version:"1.0")
    }

    @Test
    void test_parse_UnknownVariableUsedWithinDependencySpecification() {
        assert fromString("org.hibernate:hibernate-core:[:]") == dependency(group:"org.hibernate", name:"hibernate-core", version:"?")
    }

    //--------------------------------------------------------------------------
    // Helper methods
    //--------------------------------------------------------------------------
    
    private Dependency fromString(String string) {
        return ParseUtil.createDependencyFromString(APPLICATION_NAME, CONFIGURATION_NAME, string)
    }
    
    private Dependency dependency(Map map) {
        def fullMap = [applicationName:APPLICATION_NAME, configuration:CONFIGURATION_NAME] + map
        return new Dependency(fullMap)
    }

}
