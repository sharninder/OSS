/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.jts.codegen.otsidl;


/**
* com/sun/jts/codegen/otsidl/_CoordinatorResourceStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from com/sun/jts/ots.idl
* Tuesday, February 5, 2002 12:57:23 PM PST
*/


//#-----------------------------------------------------------------------------
public class _CoordinatorResourceStub extends org.omg.CORBA.portable.ObjectImpl implements com.sun.jts.codegen.otsidl.CoordinatorResource
{

  public void commit_subtransaction (org.omg.CosTransactions.Coordinator parent)
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("commit_subtransaction", true);
       org.omg.CosTransactions.CoordinatorHelper.write ($out, parent);
       $in = _invoke ($out);
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       commit_subtransaction (parent);
    } finally {
        _releaseReply ($in);
    }
  } // commit_subtransaction

  public void rollback_subtransaction ()
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("rollback_subtransaction", true);
       $in = _invoke ($out);
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       rollback_subtransaction ();
    } finally {
        _releaseReply ($in);
    }
  } // rollback_subtransaction

  public org.omg.CosTransactions.Vote prepare () throws org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("prepare", true);
       $in = _invoke ($out);
       org.omg.CosTransactions.Vote $result = org.omg.CosTransactions.VoteHelper.read ($in);
       return $result;
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       if (_id.equals ("IDL:omg.org/CosTransactions/HeuristicMixed:1.0"))
          throw org.omg.CosTransactions.HeuristicMixedHelper.read ($in);
       else if (_id.equals ("IDL:omg.org/CosTransactions/HeuristicHazard:1.0"))
          throw org.omg.CosTransactions.HeuristicHazardHelper.read ($in);
       else
            throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       return prepare ();
    } finally {
        _releaseReply ($in);
    }
  } // prepare

  public void rollback () throws org.omg.CosTransactions.HeuristicCommit, org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("rollback", true);
       $in = _invoke ($out);
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       if (_id.equals ("IDL:omg.org/CosTransactions/HeuristicCommit:1.0"))
          throw org.omg.CosTransactions.HeuristicCommitHelper.read ($in);
       else if (_id.equals ("IDL:omg.org/CosTransactions/HeuristicMixed:1.0"))
          throw org.omg.CosTransactions.HeuristicMixedHelper.read ($in);
       else if (_id.equals ("IDL:omg.org/CosTransactions/HeuristicHazard:1.0"))
          throw org.omg.CosTransactions.HeuristicHazardHelper.read ($in);
       else
            throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       rollback ();
    } finally {
        _releaseReply ($in);
    }
  } // rollback

  public void commit () throws org.omg.CosTransactions.NotPrepared, org.omg.CosTransactions.HeuristicRollback, org.omg.CosTransactions.HeuristicMixed, org.omg.CosTransactions.HeuristicHazard
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("commit", true);
       $in = _invoke ($out);
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       if (_id.equals ("IDL:omg.org/CosTransactions/NotPrepared:1.0"))
          throw org.omg.CosTransactions.NotPreparedHelper.read ($in);
       else if (_id.equals ("IDL:omg.org/CosTransactions/HeuristicRollback:1.0"))
          throw org.omg.CosTransactions.HeuristicRollbackHelper.read ($in);
       else if (_id.equals ("IDL:omg.org/CosTransactions/HeuristicMixed:1.0"))
          throw org.omg.CosTransactions.HeuristicMixedHelper.read ($in);
       else if (_id.equals ("IDL:omg.org/CosTransactions/HeuristicHazard:1.0"))
          throw org.omg.CosTransactions.HeuristicHazardHelper.read ($in);
       else
            throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       commit ();
    } finally {
        _releaseReply ($in);
    }
  } // commit

  public void commit_one_phase () throws org.omg.CosTransactions.HeuristicHazard
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("commit_one_phase", true);
       $in = _invoke ($out);
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       if (_id.equals ("IDL:omg.org/CosTransactions/HeuristicHazard:1.0"))
          throw org.omg.CosTransactions.HeuristicHazardHelper.read ($in);
       else
            throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       commit_one_phase ();
    } finally {
        _releaseReply ($in);
    }
  } // commit_one_phase

  public void forget ()
  {
    org.omg.CORBA.portable.InputStream $in = null;
    try {
       org.omg.CORBA.portable.OutputStream $out = _request ("forget", true);
       $in = _invoke ($out);
    } catch (org.omg.CORBA.portable.ApplicationException $ex) {
       $in = $ex.getInputStream ();
       String _id = $ex.getId ();
       throw new org.omg.CORBA.MARSHAL (_id);
    } catch (org.omg.CORBA.portable.RemarshalException $rm) {
       forget ();
    } finally {
        _releaseReply ($in);
    }
  } // forget

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:otsidl/CoordinatorResource:1.0", 
    "IDL:omg.org/CosTransactions/SubtransactionAwareResource:1.0", 
    "IDL:omg.org/CosTransactions/Resource:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init (args, props).string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     String str = org.omg.CORBA.ORB.init (args, props).object_to_string (this);
     s.writeUTF (str);
  }
} // class _CoordinatorResourceStub
