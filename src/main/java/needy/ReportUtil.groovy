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
package needy

import java.util.List
import java.util.Map
import java.util.Set

class ReportUtil {

	static Map<String, Set<String>> buildMapOfArtifactNameToApplicationNames(List<Dependency> dependencies) {
		def comparator = { String k1, String k2 ->
			def c1 =  k1.replace(":", " ")
			def c2 =  k2.replace(":", " ")
			return c1.compareTo(c2)
		} as Comparator
		Map<String, Set<String>> map = new TreeMap<>(comparator)
		
		dependencies.each { dependency ->
			String key = dependency.artifact.toString()
			if (!map.containsKey(key)) {
				map[key] = new TreeSet<String>()
			}
			map[key] << dependency.applicationName
		}
		return map
	}

}
