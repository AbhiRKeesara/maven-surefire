package org.apache.maven.surefire.its;

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

import java.io.File;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.surefire.its.misc.HelperAssertions;

/**
 * Test aggregator as a sibling to child modules; invokes modules as "../child"
 *
 * @author <a href="mailto:dfabulich@apache.org">Dan Fabulich</a>
 */
public class SiblingAggregatorIT
    extends AbstractSurefireIntegrationTestClass  // Special
{

    public void testSiblingAggregator()
        throws Exception
    {
        File testDir = ResourceExtractor.simpleExtractResources( getClass(), "/sibling-aggregator" );

        File aggregatorDir = new File( testDir, "aggregator" );

        Verifier verifier = new Verifier( aggregatorDir.getAbsolutePath() );
        this.executeGoal( verifier, "test" );
        verifier.verifyErrorFreeLog();
        verifier.resetStreams();

        File child2Dir = new File( testDir, "child2" );

        HelperAssertions.assertTestSuiteResults( 1, 0, 0, 0, child2Dir );
    }
}
