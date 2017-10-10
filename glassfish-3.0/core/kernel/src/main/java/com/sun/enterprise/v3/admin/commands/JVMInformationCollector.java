/*
 * The contents of this file are subject to the terms 
 * of the Common Development and Distribution License 
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at 
 * https://glassfish.dev.java.net/public/CDDLv1.0.html or
 * glassfish/bootstrap/legal/CDDLv1.0.txt.
 * See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL 
 * Header Notice in each file and include the License file 
 * at glassfish/bootstrap/legal/CDDLv1.0.txt.  
 * If applicable, add the following below the CDDL Header, 
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 */

package com.sun.enterprise.v3.admin.commands;

import com.sun.enterprise.util.SystemPropertyConstants;
import com.sun.enterprise.util.i18n.StringManager;
import java.text.NumberFormat;
import java.util.Hashtable;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

/**
 */
public class JVMInformationCollector extends StandardMBean implements JVMInformationMBean, MBeanRegistration {
    
    static final String SERVER_NAME_KEY_IN_ON = "server"; // the key to identify the server
    private MBeanServerConnection mbsc;
    private static final StringManager sm = StringManager.getManager(JVMInformationCollector.class);
    public JVMInformationCollector() throws NotCompliantMBeanException {
        super(JVMInformationMBean.class);
    }
    public String getThreadDump(final String processName) {
        final ObjectName on = processTarget(processName);
        final String title = sm.getString("thread.dump.title", getInstanceNameFromObjectName(on));
        final String td = title + "\n" + invokeMBean(on, "getThreadDump");
        return ( td );
    }

    public String getSummary(final String processName) {
        final ObjectName on = processTarget(processName);
        final String title = sm.getString("summary.title", getInstanceNameFromObjectName(on));
        final String s = title + "\n" + invokeMBean(on, "getSummary");
        return ( s );
    }

    public String getMemoryInformation(final String processName) {
        final ObjectName on = processTarget(processName);
        final String title = sm.getString("memory.info.title", getInstanceNameFromObjectName(on));
        final String mi = title + "\n" + invokeMBean(on, "getMemoryInformation");
        return ( mi );
    }

    public String getClassInformation(final String processName) {
        final ObjectName on = processTarget(processName);
        final String title = sm.getString("class.info.title", getInstanceNameFromObjectName(on));
        final String ci = title + "\n " + invokeMBean(on, "getClassInformation");
        return ( ci );
    }
    public String getLogInformation(String processName) {
        ObjectName on  = processTarget(processName);
        String title   = sm.getString("log.info.title", getInstanceNameFromObjectName(on));
        String li      = title + "\n" + invokeMBean(on, "getLogInformation");
        return ( li );
    }
    
    private ObjectName processTarget(final String processName) throws RuntimeException {
        try {
            //get the object-name of the "other" real implementation of JVMInformationMBean interface :)
            final String sn = processName == null ? SERVER_NAME_KEY_IN_ON : processName;
            final String cn = JVMInformation.class.getSimpleName();
            final ObjectName on = formObjectName(sn, cn);
            if (! this.mbsc.isRegistered(on)) {
                final String msg = sm.getString("server.unreachable", sn);
                throw new RuntimeException(msg);
            }
            return (on);
        } catch (final RuntimeException re) {
            throw(re);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeMBean(final ObjectName jvm, final String method) throws RuntimeException {
        try {
            //though proxies work fine, for now (jul 2005/8), I am not going to use them because I am not sure how they work with cascading
            //it is okay to assume that the methods in this mbean take String as parameter
            final Object[] params   = {null};
            final String[] sign     = {"java.lang.String"};
            final Object ret        = this.mbsc.invoke(jvm, method, params, sign);
            
            return ( (String) ret );
            
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void postRegister(Boolean registrationDone) {
    }

    public ObjectName preRegister(final MBeanServer server, final ObjectName name) throws Exception {
        this.mbsc = server;
        final String sn = System.getProperty(SystemPropertyConstants.SERVER_NAME);
        final ObjectName on = formObjectName(sn, JVMInformationCollector.class.getSimpleName());
        return ( on );
    }

    public void preDeregister() throws Exception {
    }

    public void postDeregister() {
    }
    
    /* package private */ static ObjectName formObjectName(final String sn, final String cName) throws Exception {
        /* domain:type=impl-class,server=target-server*/
        final String domain = "amx-internal";
        final Hashtable<String, String> props = new Hashtable<String, String> ();
        props.put("type", cName);
        props.put("category", "monitor");
        final String snk = SERVER_NAME_KEY_IN_ON;
        props.put(snk, sn);
        return ( new ObjectName(domain, props) );
    }
    
    private String getInstanceNameFromObjectName(ObjectName on) {
        return ( on.getKeyProperty(SERVER_NAME_KEY_IN_ON) );
    }
    
    static String millis2HoursMinutesSeconds(final long millis) {
        final long secmin = millis / (long) 1000;
        final long sec = secmin % 60;
        final long minhr = secmin / 60;
        final long min = minhr % 60;
        final long hr = minhr / 60;
        final String msg = sm.getString("m2hms", hr, min, sec);
        
        return ( msg );
    }
    static String millis2SecondsMillis(final long millis) {
        final long sec    = millis / (long) 1000;
        final long ms     = millis % 1000;
        final String msg  = sm.getString("m2sms", sec, ms);
        return ( msg );
    }
    static String formatLong(final long sayBytes) {
        final NumberFormat n = NumberFormat.getInstance();
        return ( n.format(sayBytes) );
    }
}