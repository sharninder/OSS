/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
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

package org.glassfish.deployment.admin;

import org.glassfish.api.ActionReport;
import org.glassfish.api.admin.AdminCommand;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.api.admin.CommandLock;
import org.glassfish.api.Param;
import org.glassfish.api.admin.ExecuteOn;
import org.glassfish.api.admin.RuntimeType;
import org.glassfish.config.support.TargetType;
import org.glassfish.config.support.CommandTarget;
import org.glassfish.deployment.common.DeploymentProperties;
import org.glassfish.deployment.common.DeploymentUtils;
import org.glassfish.internal.deployment.Deployment;
import org.jvnet.hk2.annotations.Service;
import com.sun.enterprise.config.serverbeans.Applications;
import com.sun.enterprise.config.serverbeans.Application;
import com.sun.enterprise.config.serverbeans.ApplicationRef;
import com.sun.enterprise.config.serverbeans.Domain;
import com.sun.enterprise.util.LocalStringManagerImpl;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.glassfish.deployment.versioning.VersioningException;
import org.glassfish.deployment.versioning.VersioningService;
import org.jvnet.hk2.annotations.Scoped;
import org.jvnet.hk2.component.PerLookup;
import org.jvnet.hk2.annotations.Inject;

@Service(name="show-component-status")
@Scoped(PerLookup.class)
@CommandLock(CommandLock.LockType.NONE)
@ExecuteOn(value={RuntimeType.DAS})
@TargetType(value={CommandTarget.DOMAIN, CommandTarget.DAS, CommandTarget.STANDALONE_INSTANCE, CommandTarget.CLUSTER, CommandTarget.CLUSTERED_INSTANCE})
public class ShowComponentStatusCommand implements AdminCommand {

    @Param(primary=true)
    public String name = null;

    @Param(optional=true)
    String target = "server";

    @Inject
    Deployment deployment;

    @Inject
    Domain domain;

    @Inject
    Applications applications;

    @Inject
    VersioningService versioningService;

    final private static LocalStringManagerImpl localStrings = new LocalStringManagerImpl(ListAppRefsCommand.class);    

    public void execute(AdminCommandContext context) {
        
        final ActionReport report = context.getActionReport();
        final Logger logger = context.getLogger();

        ActionReport.MessagePart part = report.getTopMessagePart();

        // retrieve matched version(s) if exist
        List<String> matchedVersions = null;
        try {
            matchedVersions = versioningService.getMatchedVersions(name, target);
        } catch (VersioningException e) {
            report.failure(logger, e.getMessage());
            return;
         }

        // if matched list is empty and no VersioningException thrown,
        // this is an unversioned behavior and the given application is not registered
        if(matchedVersions.isEmpty()){
            report.setMessage(localStrings.getLocalString("ref.not.referenced.target","Application {0} is not referenced by target {1}", name, target));
            report.setActionExitCode(ActionReport.ExitCode.FAILURE);
            return;
        }
        
         // for each matched version
        Iterator it = matchedVersions.iterator();
        while(it.hasNext()){
            String appName = (String)it.next();
            String status = "disabled";

            if (!DeploymentUtils.isDomainTarget(target)) {
                ApplicationRef ref = domain.getApplicationRefInTarget(appName, target);
                if (ref == null) {
                    report.setMessage(localStrings.getLocalString("ref.not.referenced.target","Application {0} is not referenced by target {1}", appName, target));
                    report.setActionExitCode(ActionReport.ExitCode.FAILURE);
                    return;
                }
            }
            if (domain.isAppEnabledInTarget(appName, target)) {
                status = "enabled";
            }
            
            ActionReport.MessagePart childPart = part.addChild();
            String message = localStrings.getLocalString("component.status","Status of {0} is {1}.", appName, status);
            childPart.setMessage(message);
            childPart.addProperty(DeploymentProperties.STATE, status);
        }
        report.setActionExitCode(ActionReport.ExitCode.SUCCESS);
    }
}
