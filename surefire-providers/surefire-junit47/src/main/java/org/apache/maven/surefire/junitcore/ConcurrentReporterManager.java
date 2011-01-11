package org.apache.maven.surefire.junitcore;

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

import org.apache.maven.surefire.report.ReportEntry;
import org.apache.maven.surefire.report.Reporter;
import org.apache.maven.surefire.report.ReporterConfiguration;
import org.apache.maven.surefire.report.ReporterException;
import org.apache.maven.surefire.report.ReporterFactory;
import org.apache.maven.surefire.testset.TestSetFailedException;

import java.io.IOException;
import java.util.Map;

/**
 * @author Kristian Rosenvold
 */
public abstract class ConcurrentReporterManager
    implements Reporter
{
    protected Map<String, TestSet> classMethodCounts;

    private final ReporterConfiguration reporterConfiguration;

    private final ThreadLocal<Reporter> reporterManagerThreadLocal = new ThreadLocal<Reporter>();

    protected final boolean reportImmediately;

    private final ConcurrentPrintStream out = new ConcurrentPrintStream( true );

    private final ConcurrentPrintStream err = new ConcurrentPrintStream( false );

    private ReporterFactory reporterFactory;

    public ConcurrentReporterManager( ReporterFactory reporterFactory, boolean reportImmediately,
                                      ReporterConfiguration reporterConfiguration,
                                      Map<String, TestSet> classMethodCounts )
        throws TestSetFailedException
    {
        this.reportImmediately = reportImmediately;
        this.reporterFactory = reporterFactory;
        this.reporterConfiguration = reporterConfiguration;
        this.classMethodCounts = classMethodCounts;

        // We must create the first reporterManager here, even though we will never use it.
        // There is some room for improvement here
        this.reporterFactory.createReporter();
        // Important: We must capture System.out/System.err AFTER the  reportManager captures stdout/stderr
        // because we know how to demultiplex correctly. The redirection in reporterManager is basically
        // ignored/unused because we use ConcurrentPrintStream.
        System.setOut( out );
        System.setErr( err );
    }

    public void testSetStarting( ReportEntry description )
    {
    }

    public void testSetCompleted( ReportEntry result )
        throws ReporterException
    {
        for ( TestSet testSet : classMethodCounts.values() )
        {
            testSet.replay( getReporterManager() );
        }
        try
        {
            out.writeTo( reporterConfiguration.getOriginalSystemOut() );
            err.writeTo( reporterConfiguration.getOriginalSystemErr() );
        }
        catch ( IOException e )
        {
            throw new ReporterException( "When writing reports", e );
        }
    }

    public void testAssumptionFailure( ReportEntry failure )
    {
        getOrCreateTestMethod( failure ).testAssumptionFailure( failure );
    }

    public void testFailed( ReportEntry failure )
    {
        getOrCreateTestMethod( failure ).testFailure( failure );
    }

    public void testError( ReportEntry failure )
    {
        getOrCreateTestMethod( failure ).testError( failure );
    }


    public void testSkipped( ReportEntry description )
    {
        TestSet testSet = getTestSet( description );
        TestMethod testMethod = getTestSet( description ).createTestMethod( description );
        testMethod.testIgnored( description );
        testSet.incrementFinishedTests( getReporterManager(), reportImmediately );
    }

    public void testStarting( ReportEntry description )
    {
        TestSet testSet = getTestSet( description );
        testSet.createTestMethod( description ).attachToThread();
        checkIfTestSetCanBeReported( testSet );
        testSet.attachToThread();
    }

    public void testSucceeded( ReportEntry report )
    {
        getTestMethod().testFinished();
        TestSet.getThreadTestSet().incrementFinishedTests( getReporterManager(), reportImmediately );
        detachTestMethodFromThread();
    }

    private TestMethod getOrCreateTestMethod( ReportEntry description )
    {
        TestMethod threadTestMethod = TestMethod.getThreadTestMethod();
        if ( threadTestMethod != null )
        {
            return threadTestMethod;
        }
        TestSet testSet = getTestSet( description );
        return testSet.createTestMethod( description );
    }

    public abstract void checkIfTestSetCanBeReported( TestSet testSetForTest );

    public void writeFooter( String footer )
    {
        throw new UnsupportedOperationException();
    }

    public void writeMessage( String message )
    {
        throw new UnsupportedOperationException();
    }

    public void reset()
    {
        throw new UnsupportedOperationException();
    }

    public void testFailed( ReportEntry report, String stdOut, String stdErr )
    {
        throw new UnsupportedOperationException();
    }

    public void writeDetailMessage( String message )
    {
        throw new UnsupportedOperationException();
    }

    public void testError( ReportEntry failure, String out, String err )
    {
        throw new UnsupportedOperationException();
    }

    protected TestMethod getTestMethod()
    {
        return TestMethod.getThreadTestMethod();
    }

    protected void detachTestMethodFromThread()
    {
        TestMethod.detachFromCurrentThread();
    }

    protected TestSet getTestSet( ReportEntry description )
    {
        return classMethodCounts.get( description.getSourceName() );
    }

    protected Reporter getReporterManager()
    {
        Reporter reporterManager = reporterManagerThreadLocal.get();
        if ( reporterManager == null )
        {
            reporterManager = reporterFactory.createReporter();
            reporterManagerThreadLocal.set( reporterManager );
        }
        return reporterManager;
    }

    public static ConcurrentReporterManager createInstance( Map<String, TestSet> classMethodCounts,
                                                            ReporterFactory reporterManagerFactory,
                                                            ReporterConfiguration reporterConfiguration,
                                                            boolean parallelClasses, boolean parallelBoth )
        throws TestSetFailedException
    {
        if ( parallelClasses )
        {
            return new ClassesParallelRunListener( classMethodCounts, reporterManagerFactory, reporterConfiguration );
        }
        return new MethodsParallelRunListener( classMethodCounts, reporterManagerFactory, reporterConfiguration,
                                               !parallelBoth );
    }

}
