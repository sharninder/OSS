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
package com.sun.enterprise.security.ssl;

import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509KeyManager;
import javax.security.auth.Subject;

//import com.sun.enterprise.Switch;
import com.sun.enterprise.security.common.ClientSecurityContext;
import com.sun.enterprise.security.auth.login.common.X509CertificateCredential;
//copied code from LoginContextDriver to break dependency
//import com.sun.enterprise.security.auth.login.common.LoginContextDriver;
import com.sun.enterprise.security.auth.login.common.LoginException;
import com.sun.enterprise.security.auth.login.common.PasswordCredential;
import com.sun.enterprise.security.common.AppservAccessController;
import com.sun.enterprise.security.common.SecurityConstants;
import com.sun.enterprise.security.common.Util;
import org.jvnet.hk2.component.Habitat;

import java.util.logging.*;
import com.sun.logging.*;
import java.security.PrivilegedAction;
import java.util.Set;
import javax.security.auth.login.LoginContext;

/**
 * This a J2EE specific Key Manager class that is used to select 
 * user certificates for SSL client authentication. It delegates most
 * of the functionality to the provider specific KeyManager class.
 * @author Vivek Nagar
 * @author Harpreet Singh
 */
public final class J2EEKeyManager implements X509KeyManager {

    private static Logger _logger=null;  
    static {
        _logger=LogDomains.getLogger(J2EEKeyManager.class, LogDomains.SECURITY_LOGGER);
    }

    private X509KeyManager mgr = null; // delegate
    
    private String alias = null;

    private Map tokenName2MgrMap = null;
    private boolean supportTokenAlias = false;

    private final Habitat habitat;

    public J2EEKeyManager(Habitat habitat, X509KeyManager mgr, String alias) {
        this.habitat = habitat;
        this.mgr = mgr;
	this.alias = alias;

        if (mgr instanceof UnifiedX509KeyManager) {
            UnifiedX509KeyManager umgr = (UnifiedX509KeyManager)mgr;
            X509KeyManager[] mgrs = umgr.getX509KeyManagers();
            String[] tokenNames = umgr.getTokenNames();

            tokenName2MgrMap = new HashMap();
            for (int i = 0; i < mgrs.length; i++) {
                if (tokenNames[i] != null) {
                    tokenName2MgrMap.put(tokenNames[i], mgrs[i]);
                }
            }
            supportTokenAlias = (tokenName2MgrMap.size() > 0);
        }
    }

    /**
     * Choose the client alias that will be used to select the client
     * certificate for SSL client auth.
     * @param the keytype
     * @param the certificate issuers.
     * @param the socket used for this connection. This parameter can be null,
     *        in which case the method will return the most generic alias to use.
     * @return the alias.
     */
    public String chooseClientAlias(String[] keyType, Principal[] issuers,
    Socket socket) {
        
        String alias = null;
        
        if(this.alias == null){
            //InvocationManager im = Switch.getSwitch().getInvocationManager();
            //if (im == null) {
            if(Util.getInstance().isNotServerORACC()) {
                // standalone client
                alias = mgr.chooseClientAlias(keyType, issuers, socket);
            } else {
                //ComponentInvocation ci = im.getCurrentInvocation();
                //if (ci == null) {       // 4646060
                //    throw new InvocationException();
                //}
                //Object containerContext = ci.getContainerContext();
                //if(containerContext != null &&
                //(containerContext instanceof AppContainer)) {
                   if (Util.getInstance().isACC()) {
                    ClientSecurityContext ctx = ClientSecurityContext.getCurrent();
                    Subject s = ctx.getSubject();
                    if (s == null) {
                        // pass the handler and do the login
                        //TODO V3: LoginContextDriver.doClientLogin(AppContainer.CERTIFICATE,
                        //AppContainer.getCallbackHandler());
                        doClientLogin(SecurityConstants.CERTIFICATE,
                                Util.getInstance().getCallbackHandler());
                        s = ctx.getSubject();
                    }
                    Iterator itr = s.getPrivateCredentials().iterator();
                    while(itr.hasNext()) {
                        Object o = itr.next();
                        if(o instanceof X509CertificateCredential) {
                            X509CertificateCredential crt =
                            (X509CertificateCredential) o;
                            alias = crt.getAlias();
                            break;
                        }
                    }
                }
            }
        }else{
            alias = this.alias;
        }
        if(_logger.isLoggable(Level.FINE)){
            _logger.log(Level.FINE,
            "Choose client Alias :" + alias);
        }
        return alias;
    }

    /**
     * Choose the server alias that will be used to select the server
     * certificate for SSL server auth.
     * @param the keytype
     * @param the certificate issuers.
     * @param the socket used for this connection. This parameter can be null,
     *        in which case the method will return the most generic alias to use.
     * @return the alias
     */
    public String chooseServerAlias(String keyType, Principal[] issuers,
            Socket socket) {

        String alias = null;
        if(this.alias != null){
            alias = this.alias;
        }else{
            alias =  mgr.chooseServerAlias(keyType, issuers, socket);
	}
        if(_logger.isLoggable(Level.FINE)){
            _logger.log(Level.FINE,"Choosing server alias :"+ alias);
        }         
        return alias;
    }

    /**
     * Return the certificate chain for the specified alias.
     * @param the alias.
     * @return the chain of X509 Certificates.
     */
    public X509Certificate[] getCertificateChain(String alias) {
        if(_logger.isLoggable(Level.FINE)){
            _logger.log(Level.FINE,"Getting certificate chain");
        }
        X509KeyManager keyMgr = getManagerFromToken(alias);
        if (keyMgr != null) {
            String aliasName = alias.substring(alias.indexOf(':') + 1);
            return keyMgr.getCertificateChain(aliasName);
        } else {
            return mgr.getCertificateChain(alias);
        }
    }

    /**
     * Return all the available client aliases for the specified key type.
     * @param the keytype
     * @param the certificate issuers.
     * @return the array of aliases.
     */
    public String[] getClientAliases(String keyType, Principal[] issuers) {
        if(_logger.isLoggable(Level.FINE)){
	    _logger.log(Level.FINE,"Getting client aliases");
        }
	return mgr.getClientAliases(keyType, issuers);
    }

    /**
     * Return all the available server aliases for the specified key type.
     * @param the keytype
     * @param the certificate issuers.
     * @return the array of aliases.
     */
    public String[] getServerAliases(String keyType, Principal[] issuers) {
        if(_logger.isLoggable(Level.FINE)){
            _logger.log(Level.FINE,"Getting server aliases");
        }
        return mgr.getServerAliases(keyType, issuers);
    }

    /**
     * Return the private key for the specified alias.
     * @param the alias.
     * @return the private key.
     */
    public PrivateKey getPrivateKey(String alias) {
        if(_logger.isLoggable(Level.FINE)){
	    _logger.log(Level.FINE,"Getting private key for alias:" + alias);
	}
        X509KeyManager keyMgr = getManagerFromToken(alias);
        if (keyMgr != null) {
            String aliasName = alias.substring(alias.indexOf(':') + 1);
            return keyMgr.getPrivateKey(aliasName);
        } else {
            return mgr.getPrivateKey(alias);
        }
    }    

    
    /**
     * Find the corresponding X509KeyManager associated to token in alias.
     * It returns null if there is n
     * @param tokenAlias of the form &lt;tokenName&gt;:&lt;aliasName&gt;
     */
    private X509KeyManager getManagerFromToken(String tokenAlias) {
        X509KeyManager keyMgr = null;
        int ind = -1;
        if (supportTokenAlias && tokenAlias != null && (ind = tokenAlias.indexOf(':')) != -1) {
            String tokenName = alias.substring(0, ind);
            keyMgr = (X509KeyManager)tokenName2MgrMap.get(tokenName);
        }
        return keyMgr;
    }
    
    //TODO:V3 copied all method(s)below from LoginContextDriver to break dependencies among modules
     private static final String CLIENT_JAAS_PASSWORD = "default";
    /**
     * Perform login on the client side.
     * It just simulates the login on the client side.
     * The method uses the callback handlers and generates correct
     * credential information that will be later sent to the server
     * @param int type whether it is <i> username_password</i> or 
     * <i> certificate </i> based login.
     * @param CallbackHandler the callback handler to gather user information.
     * @exception LoginException the exception thrown by the callback handler.
     */
    public  static Subject doClientLogin(int type,
                     javax.security.auth.callback.CallbackHandler jaasHandler)
        throws LoginException
    {
        final javax.security.auth.callback.CallbackHandler handler =
            jaasHandler;
        // the subject will actually be filled in with a PasswordCredential
        // required by the csiv2 layer in the LoginModule.
        // we create the dummy credential here and call the 
        // set security context. Thus, we have 2  credentials, one each for
        // the csiv2 layer and the other for the RI.
        final Subject subject = new Subject();
        //V3:Commented : TODO uncomment later for Appcontainer
        if (type == SecurityConstants.USERNAME_PASSWORD){
            AppservAccessController.doPrivileged(new PrivilegedAction() {
                public java.lang.Object run() {
                    try{
                        LoginContext lg = 
                            new LoginContext(SecurityConstants.CLIENT_JAAS_PASSWORD, 
                                             subject, handler);
                        lg.login();
                    }catch(javax.security.auth.login.LoginException e){
                        throw (LoginException)
                            new LoginException(e.toString()).initCause(e);
                    }
                    
                    return null;
                }
            });
            postClientAuth(subject, PasswordCredential.class);
            return subject;
        } else if (type == SecurityConstants.CERTIFICATE){
            AppservAccessController.doPrivileged(new PrivilegedAction() {
                public java.lang.Object run() {
                    try{
                        LoginContext lg = 
                            new LoginContext(SecurityConstants.CLIENT_JAAS_CERTIFICATE,
                                             subject, handler);
                        lg.login();
                    }catch(javax.security.auth.login.LoginException e){
                        throw (LoginException)
                            new LoginException(e.toString()).initCause(e);
                    }
                    
                    return null;
                }
            });
            postClientAuth(subject, X509CertificateCredential.class);
            return subject;
        } else if (type == SecurityConstants.ALL){
            AppservAccessController.doPrivileged(new PrivilegedAction() {
                public java.lang.Object run() {
                    try{
                        LoginContext lgup =
                            new LoginContext(SecurityConstants.CLIENT_JAAS_PASSWORD,
                                             subject, handler);
                        LoginContext lgc = 
                            new LoginContext(SecurityConstants.CLIENT_JAAS_CERTIFICATE,
                                                 subject, handler);
                        lgup.login();
                        postClientAuth(subject, PasswordCredential.class);
                        
                        lgc.login();
                        postClientAuth(subject,
                                       X509CertificateCredential.class);
                    }catch(javax.security.auth.login.LoginException e){
                        throw (LoginException)
                            new LoginException(e.toString()).initCause(e);
                    }
                    
                    return null;
                }
            });
            return subject;
        } else{ 
            AppservAccessController.doPrivileged(new PrivilegedAction() {
                public java.lang.Object run() {
                    try{
                        LoginContext lg =
                            new LoginContext(SecurityConstants.CLIENT_JAAS_PASSWORD, 
                                             subject, handler);
                        lg.login();
                        postClientAuth(subject, PasswordCredential.class);
                    }catch(javax.security.auth.login.LoginException e){
                        throw (LoginException)
                            new LoginException(e.toString()).initCause(e);
                    }
                    return null;
                }
            });
            return subject;
        }
    }
    
     /**
     * Extract the relevant username and realm information from the
     * subject and sets the correct state in the security context. The
     * relevant information is set into the Thread Local Storage from
     * which then is extracted to send over the wire.
     *
     * @param Subject the subject returned by the JAAS login.
     * @param Class the class of the credential object stored in the subject
     *
     */
    private  static void postClientAuth(Subject subject, Class clazz){
        final Class clas = clazz;
        final Subject fs = subject;
        Set credset = 
            (Set) AppservAccessController.doPrivileged(new PrivilegedAction() {
                public java.lang.Object run() {
                if(_logger.isLoggable(Level.FINEST)){
                    _logger.log(Level.FINEST,"LCD post login subject :" + fs);
                }
                    return fs.getPrivateCredentials(clas);
                }
            });
        final Iterator iter = credset.iterator();
        while(iter.hasNext()) {
            Object obj = null;    
            try{
                obj = AppservAccessController.doPrivileged(new PrivilegedAction(){
                    public java.lang.Object run(){
                        return iter.next();
                    }
                });
            } catch (Exception e){
                // should never come here 
                _logger.log(Level.SEVERE,
                            "java_security.accesscontroller_action_exception",
                            e);
            }
            if(obj instanceof PasswordCredential) {
                PasswordCredential p = (PasswordCredential) obj;
                String user = p.getUser();
                if(_logger.isLoggable(Level.FINEST)){
                    String realm = p.getRealm();
                    _logger.log(Level.FINEST,"In LCD user-pass login:" +
                            user +" realm :" + realm);
                }
                setClientSecurityContext(user, fs);
                return;
            } else if (obj instanceof X509CertificateCredential){
                X509CertificateCredential p = (X509CertificateCredential) obj;
                String user = p.getAlias();
                if(_logger.isLoggable(Level.FINEST)){
                    String realm = p.getRealm();
                    _logger.log(Level.FINEST,"In LCD cert-login::" +
                                user +" realm :" + realm);
                }
                setClientSecurityContext(user, fs);
                return;
            }
        }
    }

     /**
     * Sets the security context on the appclient side.
     * It sets the relevant information into the TLS
     * @param String username is the user who authenticated
     * @param Subject is the subject representation of the user
     * @param Credentials the credentials that the server associated with it
     */
    private static void setClientSecurityContext(String username, 
                                                 Subject subject) {
                                                 
        ClientSecurityContext securityContext =
            new ClientSecurityContext(username, subject);
        ClientSecurityContext.setCurrent(securityContext);
    }

}
