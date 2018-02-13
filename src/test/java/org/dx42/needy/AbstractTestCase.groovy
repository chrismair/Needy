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

import org.junit.After
import org.junit.Assert
import org.junit.Rule
import org.junit.rules.TestName
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Abstract superclass for tests
 *
 * @author Chris Mair
 */
@SuppressWarnings('Println')
class AbstractTestCase {

    @SuppressWarnings(['FieldName', 'LoggerWithWrongModifiers'])
    protected final Logger LOG = LoggerFactory.getLogger(getClass())

    @SuppressWarnings('PublicInstanceField')
    @Rule public TestName testName = new TestName()

    @After
    void afterAbstractTestCase() {
        println("----------[ ${getClass().getSimpleName()}.${getName()} ]----------")
    }

    @SuppressWarnings('ConfusingMethodName')
    protected void log(Object message) {
        println "[${getClass().getSimpleName()}] " + message.toString()
    }

    protected String getName() {
        return testName.getMethodName()
    }

    protected Throwable shouldFail(Closure code) {
        GroovyAssert.shouldFail(code)
    }

    protected Throwable shouldFail(Class clazz, Closure code) {
        GroovyAssert.shouldFail(clazz, code)
    }

    protected void shouldFailWithMessage(String message, Closure closure) {
        def e = shouldFail(closure)
        assert e.message?.contains(message)
    }

    protected void shouldFailWithMessage(Class theClass, String message, Closure closure) {
        def e = shouldFail(theClass, closure)
        assert e.message?.contains(message)
    }

    protected static String captureSystemOut(Closure closure) {
        def originalSystemOut = System.out
        def outputStream = new ByteArrayOutputStream()
        try {
            System.out = new PrintStream(outputStream)
            closure()
        }
        finally {
            System.out = originalSystemOut
        }
        outputStream.toString()
    }

    protected void assertSameXml(String actual, String expected) {
        Assert.assertEquals(normalizeXml(expected), normalizeXml(actual))
    }

    /**
     * Normalize the XML string. Remove all whitespace between elements, and normalize line-endings.
     * @param xml - the input XML string to normalize
     * @return the normalized XML
     */
    protected static String normalizeXml(String xml) {
        assert xml != null
        def resultXml = xml.replaceAll(/\>\s*\</, '><').trim()
        return resultXml.replace('\r\n', '\n')
    }

}
