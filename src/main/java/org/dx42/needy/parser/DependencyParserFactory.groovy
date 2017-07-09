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
 * Factory for DependencyParser objects
 * 
 * @author Chris Mair
 */
class DependencyParserFactory {

	/**
	 * Return a DependencyParser object suitable for the specified type.
	 * <p><pre>
	 * 		Type 		==> DependencyParser class
	 * 		------------------------------------------------
	 * 		"gradle" 	==> GradleDependencyParser
	 * 		null or "" 	==> GradleDependencyParser (the default)
	 * </pre>
	 * @param type - the parser type; may be null or empty 
	 */
	DependencyParser getDependencyParser(String type) {
		if (!type || type.equalsIgnoreCase("gradle")) {
			return new GradleDependencyParser()
		}
		throw new IllegalArgumentException("No such DependencyParser type [$type]")
	}
	
}
