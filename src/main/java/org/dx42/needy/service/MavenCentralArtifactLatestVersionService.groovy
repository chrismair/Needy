/*
 * Copyright 2019 the original author or authors.
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
package org.dx42.needy.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import groovy.json.JsonSlurper

/**
 * Implementation of ArtifactLatestVersionService that uses the Maven Central REST API.
 *
 * @author Chris Mair
 */
class MavenCentralArtifactLatestVersionService implements ArtifactLatestVersionService {

    private static final String BASE_URL = 'https://search.maven.org/solrsearch/select'
    private static final Logger LOG = LoggerFactory.getLogger(MavenCentralArtifactLatestVersionService)

    protected String baseUrl = BASE_URL
    private final JsonSlurper jsonSlurper = new JsonSlurper()

    @Override
    List<String> getLatestVersions(String group, String name, int numVersions) {
        String urlString = baseUrl + '?q=g:%22' + group + '%22+AND+a:%22' + name + '%22&core=gav&rows=' + numVersions + '&wt=json'
        def url = new URL(urlString)
        LOG.info("Contacting Maven Central for latest version; url=$url")

        def urlContent = getUrlContent(url)
        def json = jsonSlurper.parseText(urlContent)

        List<String> versions = json.response.docs.v
        return versions
    }

    private String getUrlContent(URL url) {
        return url.text
    }

}
