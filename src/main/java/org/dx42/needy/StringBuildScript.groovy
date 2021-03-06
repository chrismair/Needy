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
package org.dx42.needy

class StringBuildScript implements BuildScript {

    private final String text
    String type
    String description
    String componentId
    Map<String, Object> properties

    StringBuildScript(String text) {
        assert text != null, "The text value must not be null"
        this.text = text
    }

    @Override
    String getText() {
        return text
    }

}
