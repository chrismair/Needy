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

/**
 * Registry for DependencyParser objects
 * 
 * The predefined parser types are:
 * <p><pre>
 *         Type         ==> DependencyParser class
 *         ------------------------------------------------
 *         "default"     ==> GradleDependencyParser (the default)
 *         "gradle"     ==> GradleDependencyParser
 *         "grails2"     ==> GrailsBuildConfigDependencyParser
 * </pre>
 * 
 * @author Chris Mair
 */
class DependencyParserRegistry {

    private final Map<String, DependencyParser> registry = [
        (null):new GradleDependencyParser(),
        default:new GradleDependencyParser(),
        gradle:new GradleDependencyParser(),
        grails2:new GrailsBuildConfigDependencyParser()]
    
    /**
     * Return a DependencyParser object suitable for the specified (case-insensitive) type.
     * @param type - the type for the parser; case-insensitive; can be null 
     */
    DependencyParser getDependencyParser(String type) {
        def dependencyParser = registry[key(type)]
        assert dependencyParser, "No such DependencyParser type [$type]"
        return dependencyParser
    }
    
    /**
     * Register a DependencyParser for the specified type name
     * @param type - the type for the parser; case-insensitive; must not be null or empty 
     * @param dependencyParser - the DependencyParser; must not be null
     */
    void registerDependencyParser(String type, DependencyParser dependencyParser) {
        assert type
        assert dependencyParser
        registry[key(type)] = dependencyParser
    }
    
    private String key(String type) {
        return type ? type.toLowerCase() : null
    }
    
}
