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

import org.dx42.needy.parser.FileDependency
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import groovy.json.JsonSlurper

/**
 * Implementation of ArtifactLatestVersionService that uses the Maven Central REST API.
 *
 * @author Chris Mair
 */
class MavenCentralArtifactLatestVersionService implements ArtifactLatestVersionService {

    protected static final String DELAY_SYSTEM_PROPERTY = 'needy.maven-central.delay.milliseconds'
    private static final String BASE_URL = 'https://search.maven.org/solrsearch/select'
    private static final Logger LOG = LoggerFactory.getLogger(MavenCentralArtifactLatestVersionService)

    protected String baseUrl = BASE_URL
    protected Map<String, String> cachedVersions = [:]
    private final JsonSlurper jsonSlurper = new JsonSlurper()
    protected long delayMilliseconds = Long.getLong(DELAY_SYSTEM_PROPERTY, 0L)

    @Override
    String getLatestVersion(String group, String name) {
        if (group == FileDependency.GROUP) {
            return null
        }

        def cacheKey = group + ':' + name
        if (cachedVersions[cacheKey]) {
            return cachedVersions[cacheKey]
        }

        sleep(delayMilliseconds)
        def url = buildURL(group, name)
        def urlContent = getUrlContent(url)
        String version = parseVersion(urlContent)
        cachedVersions[cacheKey] = version
        return version
    }

    private String parseVersion(String urlContent) {
        def json = jsonSlurper.parseText(urlContent)
        return json.response.docs[0]?.v
    }

    private URL buildURL(String group, String name) {
        String urlString = baseUrl + '?q=g:%22' + group + '%22+AND+a:%22' + name + '%22&core=gav&rows=1&wt=json'
        return new URL(urlString)
    }

    private String getUrlContent(URL url) {
        LOG.info("Contacting Maven Central for latest version; url=$url")
        return url.text
    }

}
