/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
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
package org.glassfish.jersey.tests.integration.jersey2776;

import java.nio.charset.StandardCharsets;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.internal.MultiPartReaderClientSide;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.external.ExternalTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.AttachmentBuilder;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.junit.Ignore;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Code under test: {@link MultiPartReaderClientSide} (unquoteMediaTypeParameters)
 *
 * @author Jonatan Jönsson (jontejj at gmail.com)
 */
@Ignore
public class Jersey2776ITCase extends JerseyTest {

    @Override
    protected Application configure() {
        return new TestApplication();
    }

    @Override
    protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
        return new ExternalTestContainerFactory();
    }

    @Path("/files")
    public interface ApacheCxfMultipartClient {

        @POST
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @Produces(MediaType.TEXT_PLAIN)
        String uploadDocument(MultipartBody content);
    }

    @Test
    public void testThatMultipartServerSupportsBoundaryQuotesEvenWithInterferingRuntimeDelegate() {
        final JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
        bean.setAddress(getBaseUri().toString());
        bean.setServiceClass(ApacheCxfMultipartClient.class);
        final ApacheCxfMultipartClient cxfClient = bean.create(ApacheCxfMultipartClient.class);

        final String originalContent = "abc";
        final byte[] content = originalContent.getBytes(StandardCharsets.US_ASCII);
        final Attachment fileAttachment = new AttachmentBuilder()
                .object(content)
                .contentDisposition(new ContentDisposition("form-data; filename=\"abc-file\"; name=\"file_path\""))
                .build();

        final String fileContentReturnedFromServer = cxfClient.uploadDocument(new MultipartBody(fileAttachment));
        assertThat(fileContentReturnedFromServer, equalTo(originalContent));
    }
}
