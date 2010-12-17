package org.apache.maven.surefire.junit;

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

import junit.framework.Test;
import org.apache.maven.surefire.suite.AbstractDirectoryTestSuite;
import org.apache.maven.surefire.testset.PojoTestSet;
import org.apache.maven.surefire.testset.SurefireTestSet;
import org.apache.maven.surefire.testset.TestSetFailedException;
import org.apache.maven.surefire.util.DirectoryScanner;

import java.io.File;
import java.util.ArrayList;

/**
 * Test suite for JUnit tests based on a directory of Java test classes.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class JUnitDirectoryTestSuite
    extends AbstractDirectoryTestSuite
{
    public JUnitDirectoryTestSuite( DirectoryScanner surefireDirectoryScanner )
    {
        super( surefireDirectoryScanner );
    }

    public JUnitDirectoryTestSuite( File basedir, ArrayList includes, ArrayList excludes )
    {
        super( basedir, includes, excludes );
    }

    protected SurefireTestSet createTestSet( Class testClass, ClassLoader classLoader )
        throws TestSetFailedException
    {
        Class junitClass = getJUnitClass( classLoader );

        SurefireTestSet testSet;
        if ( junitClass != null && junitClass.isAssignableFrom( testClass ) )
        {
            testSet = new JUnitTestSet( testClass );
        }
        else if ( classHasPublicNoArgConstructor( testClass ) )
        {
            testSet = new PojoTestSet( testClass );
        }
        else
        {
            testSet = null;
        }
        return testSet;
    }

    private Class getJUnitClass( ClassLoader classLoader )
    {
        Class junitClass = null;
        try
        {
            junitClass = classLoader.loadClass( Test.class.getName() );
        }
        catch ( NoClassDefFoundError e )
        {
            // ignore this
        }
        catch ( ClassNotFoundException e )
        {
            // ignore this
        }
        return junitClass;
    }

    private boolean classHasPublicNoArgConstructor( Class testClass )
    {
        try
        {
            testClass.getConstructor( new Class[0] );
            return true;
        }
        catch ( Exception e )
        {
            return false;
        }
    }
}
