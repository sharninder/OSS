/*
  *
  *  *  Copyright 2014 Orient Technologies LTD (info(at)orientechnologies.com)
  *  *
  *  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  *  you may not use this file except in compliance with the License.
  *  *  You may obtain a copy of the License at
  *  *
  *  *       http://www.apache.org/licenses/LICENSE-2.0
  *  *
  *  *  Unless required by applicable law or agreed to in writing, software
  *  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  *  See the License for the specific language governing permissions and
  *  *  limitations under the License.
  *  *
  *  * For more information: http://www.orientechnologies.com
  *
  */
package com.orientechnologies.orient.server.plugin;

import com.orientechnologies.orient.server.OClientConnection;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.config.OServerParameterConfiguration;

/**
 * Abstract class to make OServerHandler implementation easier.
 *
 * @author Luca Garulli (l.garulli--at--orientechnologies.com)
 */
public abstract class OServerPluginAbstract implements OServerPlugin {
  protected boolean enabled = true;

  @Override
  public void startup() {
    if (!enabled)
      return;
  }

  @Override
  public void shutdown() {
  }

  @Override
  public void sendShutdown() {
    shutdown();
  }

  @Override
  public void config(OServer oServer, OServerParameterConfiguration[] iParams) {
  }

  @Override
  public void onClientConnection(final OClientConnection iConnection) {
  }

  @Override
  public void onClientDisconnection(final OClientConnection iConnection) {
  }

  @Override
  public void onBeforeClientRequest(final OClientConnection iConnection, final byte iRequestType) {
  }

  @Override
  public void onAfterClientRequest(final OClientConnection iConnection, final byte iRequestType) {
  }

  @Override
  public void onClientError(final OClientConnection iConnection, final Throwable iThrowable) {
  }

  @Override
  public Object getContent(final String iURL) {
    return null;
  }
}