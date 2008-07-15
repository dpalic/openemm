/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.target.impl;


import java.util.*;
import java.io.*;
import org.agnitas.target.*;
import org.agnitas.util.*;

/**
 *
 * @author  mhe
 */
public class TargetRepresentationImpl implements TargetRepresentation {
    
    protected ArrayList allNodes=null;
    
    private static final long serialVersionUID = -5118626285211811379L;
    
    /** Creates a new instance of TargetRepresentation */
    public TargetRepresentationImpl() {
        allNodes=new ArrayList();
    }
    
    public String generateSQL() {
        StringBuffer tmpString=new StringBuffer("");
        TargetNode tmpNode=null;
        ListIterator aIt=allNodes.listIterator();
        while(aIt.hasNext()) {
            tmpNode=(TargetNode)aIt.next();
            tmpString.append(tmpNode.generateSQL());
        }
        
        return tmpString.toString();
    }
    
    public String generateBsh() {
        StringBuffer tmpString=new StringBuffer("");
        TargetNode tmpNode=null;
        ListIterator aIt=allNodes.listIterator();
        while(aIt.hasNext()) {
            tmpNode=(TargetNode)aIt.next();
            tmpString.append(tmpNode.generateBsh());
        }
        
        return tmpString.toString();
    }
    
    public boolean checkBracketBalance() {
        int balance=0;
        TargetNode tmpNode=null;
        ListIterator aIt=allNodes.listIterator();
        while(aIt.hasNext()) {
            tmpNode=(TargetNode)aIt.next();
            if(tmpNode.isOpenBracketBefore()) {
                balance++;
            }
            if(tmpNode.isCloseBracketAfter()) {
                balance--;
            }
            if(balance<0) {
                return false;
            }
        }
        if(balance!=0) {
            return false;
        }
        
        return true;
    }
    
    public void addNode(TargetNode aNode) {
        if(aNode!=null) {
            allNodes.add(aNode);
        }
    }
    
    public void setNode(int idx, TargetNode aNode) {
        if(aNode!=null) {
            allNodes.add(idx, aNode);
        }
    }
    
    public boolean deleteNode(int index) {
        allNodes.remove(index);
        return true;
    }
    
    public ArrayList getAllNodes() {
        return allNodes;
    }
    
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField allFields=null;
        
        allFields=in.readFields();
        this.allNodes=(ArrayList)allFields.get("allNodes", new ArrayList());
        return;
    }
}
