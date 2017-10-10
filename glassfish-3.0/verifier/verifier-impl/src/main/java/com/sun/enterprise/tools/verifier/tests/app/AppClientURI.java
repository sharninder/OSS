/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.sun.enterprise.tools.verifier.tests.app;

import com.sun.enterprise.tools.verifier.tests.app.ApplicationTest;
import java.util.*;
import com.sun.enterprise.deployment.*;
import com.sun.enterprise.tools.verifier.*;
import com.sun.enterprise.tools.verifier.tests.ComponentNameConstructor;

/**
 * The java element specifies the URI of a java application
 * client module, relative to the top level of the application package.
 *
 */

public class AppClientURI extends ApplicationTest implements AppCheck {
    
    
    /**
     * The java element specifies the URI of a java application
     * client module, relative to the top level of the application package.
     *
     * @param descriptor the Application deployment descriptor
     *
     * @return <code>Result</code> the results for this assertion
     */
    public Result check(Application descriptor) {
        
        ComponentNameConstructor compName = getVerifierContext().getComponentNameConstructor();
        Result result = getInitializedResult();
        
        // java element specifies the URI of a java application
        // client module, relative to the top level of the application package
        for (Iterator itr = descriptor.getApplicationClientDescriptors().iterator(); itr.hasNext();) {
            ApplicationClientDescriptor acd = (ApplicationClientDescriptor) itr.next();
            
            // not sure what we can do to test this string?
            // as long as it's not blank, pass...
            if (!acd.getModuleDescriptor().getArchiveUri().endsWith(".jar")) {
                addErrorDetails(result, compName);
                result.failed
                        (smh.getLocalString
                        (getClass().getName() + ".failed",
                        "Error: [ {0} ] does not specify the URI [ {1} ] of an ejb-jar, relative to the top level of the application package [ {2} ], or does not end with \".jar\"",
                        new Object[] {acd.getName(), acd.getModuleDescriptor().getArchiveUri(), descriptor.getName()}));
                        
            }
        }
        if(result.getStatus() != Result.FAILED) {
            addGoodDetails(result, compName);
            result.passed
                    (smh.getLocalString
                    (getClass().getName() + ".passed",
                    "All the Application URIs are valid."));
        }
        
        return result;
    }
}