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

package org.dx42.needy.util

import org.dx42.needy.AbstractTestCase
import org.junit.Test

/**
 * Tests for WildcardUtil
 *
 * @author Chris Mair
 */
class WildcardUtilTest extends AbstractTestCase {

    // No wildcards
    
    @Test
    void test_matches_NoWildcards() {
        // Matches
        assert WildcardUtil.matches("a", "a")
        assert WildcardUtil.matches("abc", "abc")
        assert WildcardUtil.matches('!@#$%^&.()-_+=~`{}[]:;+<>', '!@#$%^&.()-_+=~`{}[]:;+<>')

        // Trims pattern strings
        assert WildcardUtil.matches("a", " a ")

        // Does not match
        assert !WildcardUtil.matches("a", "b")
        assert !WildcardUtil.matches("a", "ab")
        assert !WildcardUtil.matches("ab", "a")
    }
    
    @Test
    void test_matches_CommaSeparatedListOfPatterns_NoWildcards() {
        // Matches
        assert WildcardUtil.matches("a", "a,b")
        assert WildcardUtil.matches("abc", "x,abc,z")
        assert WildcardUtil.matches("abc", "x,y,abc")

        // Trims pattern strings
        assert WildcardUtil.matches("a", " a ,b")
        assert WildcardUtil.matches("abc", "x  ,abc, z")

        // Does not match
        assert !WildcardUtil.matches("a", "b,c")
        assert !WildcardUtil.matches("a", "b,ab")
        assert !WildcardUtil.matches("ab", "aa,bb,cc")
    }

    @Test
    void test_matches_NullOrEmptyString() {
        assert WildcardUtil.matches(null, "")
        assert WildcardUtil.matches(null, "a")
        assert WildcardUtil.matches(null, "a*")
        
        assert WildcardUtil.matches("", "")
        assert WildcardUtil.matches("", "a")
        assert WildcardUtil.matches("", "a*")
    }
    
    // Wildcards
        
    @Test
    void test_matches_Wildcards() {
        // Matches
        assert WildcardUtil.matches("a", "*")
        assert WildcardUtil.matches("abc", "*abc")
        assert WildcardUtil.matches("abc", "*abc*")
        assert WildcardUtil.matches("abc", "a*bc")
        assert WildcardUtil.matches("abc", "abc*")
        assert WildcardUtil.matches("abc", "ab*")
        assert WildcardUtil.matches("abc", "a*c")
        assert WildcardUtil.matches("abc", "*b*")

        // Trims pattern strings
        assert WildcardUtil.matches("abc", " a*c  ")
        
        // Does not match
        assert !WildcardUtil.matches("a", "b*")
        assert !WildcardUtil.matches("a", "*b")
        assert !WildcardUtil.matches("a", "*ab")
        assert !WildcardUtil.matches("ab", "ab*c")
    }
    
    @Test
    void test_matches_CommaSeparatedListOfPatterns_Wildcards() {
        // Matches
        assert WildcardUtil.matches("a", "a,*")
        assert WildcardUtil.matches("ab", "a,*,a*")
        assert WildcardUtil.matches("abc", "a,*abc")
        assert WildcardUtil.matches("abc", "ab,*abc*")
        assert WildcardUtil.matches("abc", "a*bc,aa")
        assert WildcardUtil.matches("abc", "ab,abc*")
        assert WildcardUtil.matches("abc", "b*,ab*,a*")
        assert WildcardUtil.matches("abc", "a*c,ab")
        assert WildcardUtil.matches("abc", "aaa,*b*,bbb,ab*")
        assert WildcardUtil.matches("a-c", "aaa,a*c,bbb")
        assert WildcardUtil.matches("ab@c", "aaa,a*c,bbb")

        // Trims pattern strings
        assert WildcardUtil.matches("a", "a ,* ")
        assert WildcardUtil.matches("ab", "a,  *,a*  ")
        assert WildcardUtil.matches("abc", "a, *abc")
        
        // Does not match
        assert !WildcardUtil.matches("a", "aa,b*")
        assert !WildcardUtil.matches("a", "*b,ab")
        assert !WildcardUtil.matches("a", "*ab,aa")
        assert !WildcardUtil.matches("ab", "a,ab*c,b")
    }
    
}
