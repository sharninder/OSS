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
package org.glassfish.web.admin.monitor.statistics;

import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.annotations.Scoped;
import org.jvnet.hk2.component.PerLookup;
import org.jvnet.hk2.annotations.Inject;
import org.glassfish.api.ActionReport;
import org.glassfish.api.ActionReport.ExitCode;
import org.glassfish.external.statistics.Statistic;
import org.glassfish.external.statistics.CountStatistic; 
import org.glassfish.external.statistics.RangeStatistic; 
import org.glassfish.external.statistics.TimeStatistic;
import org.glassfish.external.statistics.Stats;
import org.glassfish.external.statistics.StringStatistic;
import org.glassfish.admin.monitor.cli.MonitorContract;
import org.glassfish.flashlight.datatree.TreeNode;
import org.glassfish.flashlight.MonitoringRuntimeDataRegistry;
import org.glassfish.api.Param;
import java.util.List;
import com.sun.enterprise.util.LocalStringManagerImpl;

/** 
 *
 * For v3 Prelude, following stats will be available
 *
 * asc activeSessionsCount, 
 * ast activatedSessionsTotal, 
 * rst rejectedSessionsTotal, 
 * st  sessionsTotal
 * ajlc activeJspsLoadedCount, 
 * mjlc maxJspsLoadedCount, 
 * tjlc totalJspsLoadedCount
 * aslc activeServletsLoadedCount, 
 * mslc maxServletsLoadedCount, 
 * tslc totalServletsLoadedCount
 *
 * ash activeSessionsHigh, 
 * est expiredSessionsTotal, 
 * pvst passivatedSessionsTotal, 
 * pst persistedSessionsTotal, 
 */
@Service
@Scoped(PerLookup.class)
public class WebModuleVirtualServerStatsImpl implements MonitorContract {

    // app name otherwise web
    @Param (optional=true)
    String appName;

    @Inject
    private MonitoringRuntimeDataRegistry mrdr;

    private final LocalStringManagerImpl localStrings = 
        new LocalStringManagerImpl(WebModuleVirtualServerStatsImpl.class);

    private final String name = "webmodule";

    private final String displayFormat = 
    "%1$-5s %2$-5s %3$-5s %4$-5s %5$-5s %6$-5s %7$-5s %8$-8s %9$-10s %10$-5s";

    public String getName() {
        return name;
    }

    public ActionReport process(final ActionReport report, final String filter) {

        if (mrdr == null) {
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            report.setMessage(localStrings.getLocalString("mrdr.null", 
                "MonitoringRuntimeDataRegistry is null"));
            return report;
        }

        TreeNode serverNode = mrdr.get("server");
        if (serverNode == null) {
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            report.setMessage(localStrings.getLocalString("mrdr.null", 
                "MonitoringRuntimeDataRegistry server node is null"));
            return report;
        }

        String [] patternArr;
            
        if (appName != null) {
            // post prelude - need to fix this for virtual server 
            patternArr = new String [] {"server.applications." + appName + ".*.*"};
        } else {
            patternArr = new String [] {"server.web.session.*", 
                    "server.web.servlet.*", "server.web.jsp.*"};
        }
        
	long activeSessionsCount = 0; 
	long sessionsTotal = 0;
	long rejectedSessionsTotal = 0; 
	long activatedSessionsTotal = 0; 

	long activeJspsLoadedCount = 0; 
	long maxJspsLoadedCount = 0; 
	long totalJspsLoadedCount = 0;

	long activeServletsLoadedCount = 0; 
	long maxServletsLoadedCount = 0; 
	long totalServletsLoadedCount = 0;

        long lval = 0;

        for (String pattern : patternArr) {
            List<TreeNode> tnL = serverNode.getNodes(pattern);
            for (TreeNode tn : tnL) {
                if (tn.hasChildNodes()) {
                    continue;
                }

                if ("activesessionscount".equals(tn.getName())) { 
                    activeSessionsCount = getRangeStatisticValue(tn.getValue());
                } else if ("activatedsessionstotal".equals(tn.getName())) { 
                    activatedSessionsTotal = getCountStatisticValue(tn.getValue());
                } else if ("rejectedsessionstotal".equals(tn.getName())) { 
                    rejectedSessionsTotal = getCountStatisticValue(tn.getValue());
                } else if ("sessionstotal".equals(tn.getName())) { 
                    sessionsTotal = getCountStatisticValue(tn.getValue());
                } else if ("activejspsloadedcount".equals(tn.getName())) { 
                    activeJspsLoadedCount = getRangeStatisticValue(tn.getValue());
                } else if ("maxjspsloadedcount".equals(tn.getName())) { 
                    maxJspsLoadedCount = getCountStatisticValue(tn.getValue());
                } else if ("totaljspsloadedcount".equals(tn.getName())) { 
                    totalJspsLoadedCount = getCountStatisticValue(tn.getValue());
                } else if ("activeservletsloadedcount".equals(tn.getName())) { 
                    activeServletsLoadedCount = getRangeStatisticValue(tn.getValue());
                } else if ("maxservletsloadedcount".equals(tn.getName())) { 
                    maxServletsLoadedCount = getCountStatisticValue(tn.getValue());
                } else if ("totalservletsloadedcount".equals(tn.getName())) { 
                    totalServletsLoadedCount = getCountStatisticValue(tn.getValue());
                }
            }

        }

        report.setMessage(String.format(displayFormat, 
                activeSessionsCount, activatedSessionsTotal,
                rejectedSessionsTotal, sessionsTotal,
                activeJspsLoadedCount, maxJspsLoadedCount, 
                totalJspsLoadedCount,
                activeServletsLoadedCount, maxServletsLoadedCount,
                totalServletsLoadedCount));

        report.setActionExitCode(ExitCode.SUCCESS);
        return report;
    }


    /**
     * Gets the total number of rejected sessions for the web
     * module associated with this WebModuleStats.
     *
     * <p>This is the number of sessions that were not created because the
     * maximum allowed number of sessions were active.
     *.
     * @return Total number of rejected sessions
     */
    public CountStatistic getRejectedSessionsTotal() {
        return null;
    }
	
    /**
     * Gets the total number of expired sessions for the web
     * module associated with this WebModuleStats.
     *.
     * @return Total number of expired sessions
     */
    public CountStatistic getExpiredSessionsTotal() {
        return null;
    }
	
    /**
     * Gets the maximum number of concurrently active sessions for the web
     * module associated with this WebModuleStats.
     *
     * @return Maximum number of concurrently active sessions
     */
    public CountStatistic getActiveSessionsHigh() {
        return null;
    }
	
    /**
     * Gets the number of currently active sessions for the web
     * module associated with this WebModuleStats.
     *.
     * @return Number of currently active sessions
     */
    public CountStatistic getactiveSessionsCount() {
        return null;
    }
	
    /**
     * Gets the total number of sessions that have been created for the web
     * module associated with this WebModuleStats.
     *.
     * @return Total number of sessions created
     */
    public CountStatistic getSessionsTotal() {
        return null;
    }
	
    /**
     * Gets the number of JSPs that have been loaded in the web module
     * associated with this WebModuleStats.
     *.
     * @return Number of JSPs that have been loaded
     */
    public CountStatistic getJSPCount() {
        return null;
    }
	

    /**
     * Gets the number of JSPs that have been reloaded in the web module
     * associated with this WebModuleStats
     *.
     * @return Number of JSPs that have been reloaded
     */
    public CountStatistic getJSPReloadCount() {
        return null;
    }
	
    /**
     * Gets the number of errors that were triggered by JSP invocations.
     *.
     * @return Number of errors triggered by JSP invocations
     */
    public CountStatistic getJSPErrorCount() {
        return null;
    }
    
    /**
     */
    public CountStatistic getPassivatedSessionsCurrent() {
        return null;
    }
	
    /**
     * Gets the cumulative processing times of all servlets in the web module
     * associated with this WebModuleStats.
     *
     * @return Cumulative processing times of all servlets in the web module
     * associated with this WebModuleStats
     */
    public CountStatistic getServletProcessingTimes() {
        return null;
    }
    
    /**
     * Returns comma-separated list of all sessions currently active in the web
     * module associated with this WebModuleStats.
     *
     * @return Comma-separated list of all sessions currently active in the
     * web module associated with this WebModuleStats
     */
    public StringStatistic getSessions() {
        return null;
    }
    
    public Statistic[] getStatistics() {
    	return null;
    }

    public String[] getStatisticNames() {
    	return null;
    }

    public Statistic getStatistic(String statisticName) {
    	return null;
    }

    private long getCountStatisticValue(Object obj) {
        long l = 0L;
        if (obj == null) return l;
        if (obj instanceof CountStatistic) {
            return ((CountStatistic)obj).getCount();
        }
        return l;
    }

    private long getRangeStatisticValue(Object obj) {
        long l = 0L;
        if (obj == null) return l;
        if (obj instanceof RangeStatistic) {
            return ((RangeStatistic)obj).getCurrent();
        }
        return l;
    }
}
