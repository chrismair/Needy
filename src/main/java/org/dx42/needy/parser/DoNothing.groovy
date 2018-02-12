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
 * Placeholder object that ignores methods calls and returns itself for property accesses
 *
 * @author Chris Mair
 */
@SuppressWarnings('UnusedMethodParameter')
class DoNothing extends Expando {

    static final INSTANCE = new DoNothing()
    
    def methodMissing(String name, args) {
        return INSTANCE
    }
    
    def propertyMissing(String name) {
        return INSTANCE
    }

    @Override
    String toString() {
        return "?"
    }

}
