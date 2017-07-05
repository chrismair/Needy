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
package dx42.needy

import groovy.transform.Immutable

@Immutable
class UrlBuildScript implements BuildScript {

	String url
	String type
	String description
	String componentId
	
	@Override
	String getText() {
		return asURL().text
	}

	@Override
	String toString() {
		return "UrlBuildScript(url=$url)"
	}

	protected URL asURL() {
		assert url, "The url must not be null or empty"
		return new URL(url)
	}
		
}
