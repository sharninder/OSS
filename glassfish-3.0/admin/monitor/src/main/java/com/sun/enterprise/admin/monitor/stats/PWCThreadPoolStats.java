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

/*
 * Copyright 2004-2005 Sun Microsystems, Inc.  All rights reserved.
 * Use is subject to license terms.
 */

/*
 * PWCThreadPoolStats.java
 *
 * Created on April 2, 2004, 10:19 AM
 */

package com.sun.enterprise.admin.monitor.stats;

/**
 *
 * @author  nsegura
 */
import org.glassfish.j2ee.statistics.Stats;
import org.glassfish.j2ee.statistics.CountStatistic;
import com.sun.enterprise.admin.monitor.stats.StringStatistic;

/** 
 * Returns the statistical information associated with 
 * the HttpService thread pool 
 */
public interface PWCThreadPoolStats extends Stats {
    
    /** 
     * Returns the thread pool Id
     * @return id
     */    
    public StringStatistic getId();
    
    /** 
     * Returns the number of threads that are currently idle
     * @return idle threads
     */    
    public CountStatistic getCountThreadsIdle();
    
    /** 
     * Returns current number of threads
     * @return current threads
     */    
    public CountStatistic getCountThreads();
    
    /** 
     * Returns the maximum number of native threads allowed in the thread pool
     * @return max number of threads allowed
     */    
    public CountStatistic getMaxThreads();
    
    /** 
     * Returns the current number of requests waiting for a native thread
     * @return queued requests
     */    
    public CountStatistic getCountQueued();
    
    /** 
     * Returns the highest number of requests that were ever queued up
     * simultaneously for the use of a native thread since the server
     * was started
     */    
    public CountStatistic getPeakQueued();
    
    /** 
     * Returns the maximum number of requests that can be queued at one
     * time to wait for a native thread
     * @return max number of request to be queued
     */    
    public CountStatistic getMaxQueued();
    
}