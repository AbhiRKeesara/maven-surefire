/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.surefire.junitcore;

import org.junit.Test;

/*
 * Copyright 2002-2009 the original author or authors.
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
 *
 * Also licensed under CPL http://junit.sourceforge.net/cpl-v10.html
 */


import static junit.framework.Assert.*;

import java.util.Map;

/*
 * @author Kristian Rosenvold, kristian.rosenvold@gmail com
 */

public class TestsToRunTest {
    @Test
    public void testGetTestSets() throws Exception {
        TestsToRun testsToRun = new TestsToRun(T1.class, T2.class);
        assertEquals( 2,  testsToRun.size());
        JUnitCoreTestSet coreTestSet = testsToRun.getTestSet("org.apache.maven.surefire.junitcore.TestsToRunTest$T1");
        assertNotNull( coreTestSet);
        assertEquals(T1.class, coreTestSet.getTestClass());
        Map<String,JUnitCoreTestSet> stringJUnitCoreTestSetMap = testsToRun.getTestSets();
        assertEquals(2, stringJUnitCoreTestSetMap.size());
    }

    class T1 {
        
    }
    class T2 {

    }
}
