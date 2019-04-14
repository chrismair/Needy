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

import org.dx42.needy.AbstractTestCase
import org.junit.Test

/**
 * Integration tests for MavenCentralArtifactLatestVersionService
 *
 * @author Chris Mair
 */
class MavenCentralArtifactLatestVersionServiceTest extends AbstractTestCase {

    private static final String GROUP = 'org.codenarc'
    private static final String NAME = 'CodeNarc'

    private MavenCentralArtifactLatestVersionService service = new MavenCentralArtifactLatestVersionService()

    @Test
    void test_ImplementsArtifactLatestVersionService() {
        assert service instanceof ArtifactLatestVersionService
    }

    @Test
    void test_getLatestVersion() {
        def latestVersion = service.getLatestVersion(GROUP, NAME)
        log("latestVersion=$latestVersion")
        assert latestVersion.contains('1.3')
    }

    @Test
    void test_getLatestVersion_NoMatchingArtifact() {
        def latestVersion = service.getLatestVersion(GROUP, 'NoSuchName')
        log("latestVersion=$latestVersion")
        assert latestVersion == null
    }

    @Test
    void test_getLatestVersion_Error() {
        service.baseUrl = 'http://NoSuchHost'
        shouldFail(IOException) { service.getLatestVersion(GROUP, NAME) }
    }

    @Test
    void test_UsesCachedVersions() {
        // Stores version in the cache
        def latestVersion = service.getLatestVersion(GROUP, NAME)
        def key = GROUP + ':' + NAME
        assert service.cachedVersions[key] == latestVersion

        // Retrieves version from the cache
        service.cachedVersions['a:b'] = 'v1.2'
        assert service.getLatestVersion('a', 'b') == 'v1.2'
    }

}
