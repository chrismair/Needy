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

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Needy {

    public static final String DEFAULT_CONFIG_FILE = "config.needy"
    private static final Logger LOG = LoggerFactory.getLogger(Needy)

    // Abstract creation of instance dependencies to allow substitution of test spy for unit tests
    protected Closure createNeedyRunner = { new NeedyRunner() }
    protected Closure createNeedyConfiguration = { filename -> DslNeedyConfiguration.fromFile(filename) }

    static void main(String[] args) {
        LOG.info("Needy command-line")
        def needy = new Needy()
        needy.execute(args)
    }

    @SuppressWarnings('UnusedMethodParameter')
    protected void execute(String[] args) {
        String configFile = args.size() > 0 ? args[0] : DEFAULT_CONFIG_FILE
        def needyConfiguration = createNeedyConfiguration(configFile)
        def needyRunner = createNeedyRunner()
        needyRunner.setNeedyConfiguration(needyConfiguration)
        needyRunner.execute()
    }

}
