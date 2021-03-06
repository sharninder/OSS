/**
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
* Copyright 2009 Sun Microsystems, Inc. All rights reserved.
* Generated code from the com.sun.enterprise.config.serverbeans.*
* config beans, based on  HK2 meta model for these beans
* see generator at org.admin.admin.rest.GeneratorResource
* date=Wed Aug 26 14:38:42 PDT 2009
* Very soon, this generated code will be replace by asm or even better...more dynamic logic.
* Ludovic Champenois ludo@dev.java.net
*
**/
package org.glassfish.admin.rest.resources;
import javax.ws.rs.*;

import org.glassfish.admin.rest.TemplateListOfResource;
import org.glassfish.api.monitoring.ContainerMonitoring;

public class ListMonitoringItemResource extends TemplateListOfResource<ContainerMonitoring> {


	@Path("{Name}/")
	public MonitoringItemResource getMonitoringItemResource(@PathParam("Name") String id) {
		MonitoringItemResource resource = resourceContext.getResource(MonitoringItemResource.class);
		for (ContainerMonitoring c: entity){
			if(c.getName().equals(id)){
				resource.setEntity(c);
			}
		}
		return resource;
	}


public String getPostCommand() {
	return null;
}
}
