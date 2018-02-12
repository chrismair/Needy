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

import org.dx42.needy.Dependency
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Contains static utility methods related to parsing
 *
 * @author Chris Mair
 */
class ParseUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ParseUtil)
    
    protected static Dependency createDependencyFromString(String applicationName, String configurationName, String str) {
        assert str.size() > 0, "String format dependency error - empty string"
        def s = removeUnknownProperties(str)
        def strings = s.tokenize(':')

        def group = (strings.size() > 2) ? strings[0] : null
        def artifactName = (strings.size() < 3) ? strings[0] : strings[1]
        def version = (strings.size() == 2) ? strings[1] : strings[2]
        version = scrubVersion(version)
    
        return new Dependency([applicationName:applicationName, group:group, name:artifactName, version:version, configuration:configurationName])
    }
    
    private static String removeUnknownProperties(String s) {
        return s.replace("[:]", "?")
    }
    
    private static String scrubVersion(String version) {
        if (version?.contains('@')) {
            return version.substring(0, version.indexOf('@'))
        }
        return version
    }
    
    protected static Closure ignoreEverything() {
        def inner = { n ->
            LOG.info("inner for [$n]")
            return [:]
        }
        def ignoreEverything = { n ->
            LOG.info("ignoreEverything for [$n]")
            return [:].withDefault(inner)
        }
        return ignoreEverything
    }

}
