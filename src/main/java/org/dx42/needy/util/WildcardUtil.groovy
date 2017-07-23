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

package org.dx42.needy.util

/**
 * Static utility methods related to wildcard pattern matching.
 *
 * @author Chris Mair
 */
class WildcardUtil {

	/**
	 * Return true if the string matches a single pattern or a comma-separated list of patterns, 
	 * optionally containing one or more wildcards ("*"). If string is null or empty, always return true.
	 * 
	 * @param string - the string to match against the pattern(s); if null or empty return true
	 * @param wildcardPattern - the pattern string, optionally including wildcard characters ('*');
     *      may optionally contain more than one pattern, separated by commas; may be null or empty to always match.
     *      The pattern string or strings (if comma-separated) are trimmed before comparison.
	 * @return true if the string matches the wildcardPattern
	 */
	static boolean matches(String string, String wildcardPattern) {
		if (!string) {
			return true
		}
		
		List<String> patterns = wildcardPattern ? wildcardPattern.tokenize(',') : []
		List<String> regexes = patterns.collect { String pattern ->	convertWildcardsStringToRegex(pattern) }
		regexes.find { regex -> string ==~ regex }
	}
	
	private static String convertWildcardsStringToRegex(String stringWithWildcards) {
		def result = new StringBuffer()
		stringWithWildcards.trim().each { ch ->
			switch (ch) {
				case '*':
					result << ".*"
					break
				case ['$', '|', '[', ']', '(', ')', '.', ':', '{', '}', '\\', '^', '+']:
					result << '\\' + ch
					break
				default: result << ch
			}
		}
		return result
	}
	
	// Private constructor. All members are static.
	private WildcardUtil() { }
	
}
