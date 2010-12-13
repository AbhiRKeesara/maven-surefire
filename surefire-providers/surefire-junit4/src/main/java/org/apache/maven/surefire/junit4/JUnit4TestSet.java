package org.apache.maven.surefire.junit4;

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

import org.apache.maven.surefire.report.ReporterManager;
import org.apache.maven.surefire.testset.AbstractTestSet;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import java.util.List;

public class JUnit4TestSet
    extends AbstractTestSet
{
    private final List<RunListener> customRunListeners;

    /**
     * Constructor.
     *
     * @param testClass the class to be run as a test
     */
    protected JUnit4TestSet( Class testClass, List<RunListener> customRunListeners )
    {
        super( testClass );
        this.customRunListeners = customRunListeners;
    }

    /**
     * Actually runs the test and adds the tests results to the <code>reportManager</code>.
     *
     * @see org.apache.maven.surefire.testset.SurefireTestSet#execute(org.apache.maven.surefire.report.ReporterManager, java.lang.ClassLoader)
     */
    public void execute( ReporterManager reportManager, ClassLoader loader )
        throws TestSetFailedException
    {
        Runner junitTestRunner = Request.aClass( getTestClass() ).getRunner();

        RunNotifier fNotifier = new RunNotifier();
        RunListener listener = new JUnit4TestSetReporter( this, reportManager );
        fNotifier.addListener( listener );

        if ( customRunListeners != null )
        {
            for ( RunListener customRunListener : customRunListeners )
            {
                fNotifier.addListener( customRunListener );
            }
        }

        try
        {
            junitTestRunner.run( fNotifier );
        }
        finally
        {
            fNotifier.removeListener( listener );

            if ( customRunListeners != null )
            {
                for ( RunListener customRunListener : customRunListeners )
                {
                    fNotifier.removeListener( customRunListener );
                }
            }
        }
    }

}
