/* *******************************************************************
 * Copyright (c) 1999-2001 Xerox Corporation, 
 *               2002 Palo Alto Research Center, Incorporated (PARC).
 * All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 * Contributors: 
 *     Xerox/PARC     initial implementation 
 * ******************************************************************/


package org.aspectj.ajde.internal;
 
import junit.framework.*;

public class AjdeInternalTests extends TestCase {

    public static Test suite() { 
        TestSuite suite = new TestSuite(AjdeInternalTests.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(AspectJBuildManagerTest.class); 
        suite.addTestSuite(LstBuildConfigManagerTest.class); 
        //$JUnit-END$
        return suite;
    }

    public AjdeInternalTests(String name) { super(name); }

}  
