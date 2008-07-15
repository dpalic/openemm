/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/
package org.agnitas.util;


import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/*
// TagString
//
//for Tags
// <agnPROFILE>
// <agnUNSUBCRIBE>
// <agnAUTOURL url=45535>
 */
public class TagString {
    
    public String url1 ="http://h1.rdir.de/"; // aus datenbank
    public String url2 ="http://h2.rdir.de/"; // aus datenbank
    public String url3 ="http://h3.rdir.de/"; // aus datenbank
    public String url4 ="http://h4.rdir.de/"; // aus datenbank for One-Pixel-GIF
    public String url5 ="http://h5.rdir.de/"; // aus datenbank
    
    public long XOR_KEY; // = 12134;
    
    public long company_id;
    public long mailing_id;
    public long customer_id;
    public long parameter=0;
    
    String company_crypt=null;
    String mailing_crypt=null;
    String mailing_clear=null;
    
    private MessageDigest md = null;
    
    // contructor
    //
    public TagString(long xor_key, long company_id, long mailing_id) throws Exception{
        this();
        this.XOR_KEY = xor_key;
        this.company_id = company_id;
        this.mailing_id = mailing_id;
        
        company_crypt = to_hex_new(company_id, 8);
        mailing_crypt = to_hex_new(mailing_id, 8);
        mailing_clear = add_zeros(Long.toHexString(mailing_id), 8);
    }
    
    // empty Constructor -- for decrypt
    public TagString() throws Exception {
        md = MessageDigest.getInstance("MD5");
    }
    
    public void set_xor_key(long xor_key){
        XOR_KEY = xor_key;
    }
    
    // encrypt field variables -- helper function
    public void crypt() throws Exception{
        company_crypt = to_hex_new(company_id, 8);
        mailing_crypt = to_hex_new(mailing_id, 8);
        mailing_clear = add_zeros(Long.toHexString(mailing_id), 8);
    }
    
    public String toString(){
        return "company_id: " + company_id + "\n" +
        "mailing_id: " + mailing_id + "\n" +
        "customer_id: " + customer_id + "\n" +
        "parameter: " + parameter;
        
    }
    
    //
    // conveniance methods.
    // presumes: company id and mailing id was set in constructor
    //
    
    // returns: encrypted url
    public String make_profile_url(long customer_id) throws Exception{
        return url1 + encrypt(customer_id);
    }
    
    // returns: encrypted url
    public String make_unsubscribe_url(long customer_id) throws Exception{
        return url2 + encrypt(customer_id);
    }
    
    // returns: encrypted url
    public String make_onepixel_url(long customer_id) throws Exception{
        return url4 + encrypt(customer_id);
    }
    
    /** returns: ecrypted auto_url */
    public String make_autourl(long customer_id, long parameter) throws Exception {
        return url3 + encrypt_autourl(customer_id, parameter);
    }
    
    public String make_archive_url(long customer_id) throws Exception {
        return url5 + encrypt(customer_id);
    }
    
    /** convert to decimal and do XOR */
    public long to_decimal(String hex_value) throws Exception {
        try {
            return (Long.parseLong(hex_value, 16) ^ XOR_KEY);
        } catch(Exception e) {
            return 0;
        }
    }
    
    /** convert to decimal and don't XOR */
    public long to_decimal_new(String hex_value) throws Exception {
        try {
            return (Long.parseLong(hex_value, 16) ^ 0x5CF16053L);
        } catch(Exception e) {
            return 0;
        }
    }
    //
    /** convert long to hex and do XOR */
    //
    public String to_hex(long number, int length) throws Exception{
        
        // convert to hexadecimal
        String text = Long.toHexString(number ^ XOR_KEY);
        
        // add zeros until long enough
        return add_zeros(text, length);
        
    }
    
    //
    /** convert long to hex and don't XOR */
    //
    public String to_hex_new(long number, int length) throws Exception{
        
        // convert to hexadecimal
        String text = Long.toHexString(number ^ 0x5CF16053L);
        
        // add zeros until long enough
        return add_zeros(text, length);
        
    }
    //
    /** add prefix zeros to hex string
     * Arg: result length of string
     */
    public String add_zeros(String text, int length) throws Exception {
        int text_length = text.length();
        
        if(text_length > length){
            System.err.println("Error: value " + text + " too big.");
            throw new Exception("Error: value" + text + " bigger than" + length);
            // good place for some nice error management
            //System.exit(8);
        }

        if(text_length == length){
            return text;
       	}
        
        String result=text;
        
       	for(int i=text_length; i != length; i++){
            result="0" + result;
       	}
        return result;
    }
    
    //
    /** function to calculate the hashcode
     * // this should be replaced by something clever
     */
    private long get_hashcode(long customer_id){
        return (company_id * 117) + mailing_id + customer_id;
    }
    
    private String get_new_hashcode(long customer_id){
        String hashBase=new String("agn"+this.mailing_id+"a"+this.parameter+"g"+customer_id+"n"+this.XOR_KEY+"i"+this.company_id+"t");
        StringBuffer hashResult=new StringBuffer();
        byte[] tmp;
        
        try {
            md.reset();
            md.update(hashBase.getBytes("US-ASCII"));
            tmp=md.digest();
            Byte aByte=null;
            for(int i=0; i<tmp.length; i=i+4) {
                aByte=new Byte(tmp[i]);
                // System.out.println(add_zeros(Integer.toHexString(aByte.intValue()&255),2));
                hashResult.append(add_zeros(Integer.toHexString(aByte.intValue()&255),2));
            }
        } catch (Exception e) {
            System.out.println("couldn't make digest of partial content: "+e);
        }
        
        // System.out.println("hashbase: "+hashBase);
        // System.out.println("hash: "+hashResult.toString());
        return hashResult.toString();
    }
    
    // http://testserver.agnitas.local/cs?0000006b5cf1604d5cf1405023d0e0ecgS
    // http://testserver.agnitas.local/cs?0000006b5cf1604d5cf14057c0263c7egS
    // http://testserver.agnitas.local/cs?0000006b5cf1604d5cf1405637ce45e9gS
    // http://testserver.agnitas.local/cs?0000006b5cf1604d5cf1405502ab88ecgS
    
    // http://testserver.agnitas.local/cs?0000006b0008aceb0008b303882e2S
    // http://testserver.agnitas.local/cs?0000006b0008aceb0008b302882edS
    
    /** returns the mailing id from an encrypted string*/
    public long get_mailing_id(String url_string){
        try {
            mailing_id = Long.parseLong(url_string.substring(0,8), 16);
        } catch(Exception e) {
            mailing_id=0;
        }
        return mailing_id;
    }
    
    /** returns the company id from an encrypted string*/
    public long get_company_id(String url_string){
        try {
            company_id = Long.parseLong(url_string.substring(0,8), 16);
        } catch(Exception e) {
            company_id=0;
        }
        return company_id;
    }
    
    // decrypt with specified XOR
    // Arg: string, XOR_KEY
    public boolean decrypt(String url_string, long xor_key){
        set_xor_key(xor_key);
        return decrypt(url_string);
    }
    
    //
    // decrypt string
    //  - store results in field variables
    // Arg.: encoded hex-string
    
    public boolean decrypt(String url_string){
        long hash_value=0;
        long check_hash=0;
        String hash_value2="";
        String check_hash2="";
        boolean isNewHash=false;
        
        if(url_string.endsWith("g")) {
            url_string=url_string.substring(0, url_string.length()-1);
            isNewHash=true;
        }
        
        try {
            mailing_id = Long.parseLong(url_string.substring(0,8), 16) ;
            
            if(!isNewHash) {
                System.out.println("old hash, no longer valid");
                return false;
                /*
                company_id = to_decimal(url_string.substring(8,16));
                customer_id = to_decimal(url_string.substring(16,24));
                hash_value = to_decimal(url_string.substring(24));
                check_hash = get_hashcode(customer_id); */
            } else {
                // System.out.println("new hash");
                company_id = to_decimal_new(url_string.substring(8,16));
                customer_id = to_decimal_new(url_string.substring(16,24));
                hash_value2 = url_string.substring(24);
                check_hash2 = get_new_hashcode(customer_id);
            }
        } catch (Exception e) {
            System.out.println("ex normal: "+e);
            return false;
        }
        if(!isNewHash) {
            if(hash_value != check_hash){
                System.out.println("wrong hash normal: "+hash_value+" "+check_hash);
                return false;
            }
        } else {
            if(hash_value2.compareTo(check_hash2)!=0){
                System.out.println("wrong hash new: "+hash_value2+" "+check_hash2);
                return false;
            }
        }
        
        return true;
    }
    
    // decrypt auto_url
    // args: string, XOR_KEY
    public boolean decrypt_autourl(String url_string, long xor_key){
        set_xor_key(xor_key);
        return decrypt_autourl(url_string);
    }
    
    // decrypt autourl
    // same as decrypt, with parameter string
    // Arg.: encoded hex-string
    //
    public boolean decrypt_autourl(String url_string){
        long hash_value=0;
        long check_hash=0;
        String hash_value2="";
        String check_hash2="";
        
        boolean isNewHash=false;
        
        if(url_string.endsWith("g")) {
            url_string=url_string.substring(0, url_string.length()-1);
            isNewHash=true;
        }
        // 00003f5f0008ace10dee7b360002a9d8ffffffffbd3260ebg
        
        try {
            mailing_id = Long.parseLong(url_string.substring(0,8), 16);
            
            if(!isNewHash) {
                // System.out.println("old hash");
                company_id = to_decimal(url_string.substring(8,16));
                customer_id = to_decimal(url_string.substring(16,24));
                parameter = to_decimal(url_string.substring(24,32));
                hash_value = to_decimal(url_string.substring(32));
                check_hash = get_hashcode(customer_id+parameter);
            } else {
                // System.out.println("new hash");
                company_id = to_decimal_new(url_string.substring(8,16));
                customer_id = to_decimal_new(url_string.substring(16,24));
                parameter = to_decimal_new(url_string.substring(24,32));
                hash_value2 = url_string.substring(32);
                check_hash2 = get_new_hashcode(customer_id+parameter);
            }
        } catch (Exception e) {
            System.out.println("ex auto: "+e);
            return false;
        }
        
        if(!isNewHash) {
            if(hash_value != check_hash){
                System.out.println("wrong hash auto: "+hash_value+" "+check_hash);
                return false;
            }
        } else {
            if(hash_value2.compareTo(check_hash2)!=0){
                System.out.println("wrong hash auto: "+hash_value2+" "+check_hash2);
                return false;
            }
        }
        
        return true;
    }
    
    
    // decrypt deeptracking
    // same as decrypt, with parameter string
    // Arg.: encoded hex-string
    //
    public boolean decryptByCompanyID(String url_string){
        String hash_value2="";
        String check_hash2="";
        
        if(!url_string.endsWith("gc")) {
            return false;
        }
        
        url_string=url_string.substring(0, url_string.length()-2);
        // 00003f5f0008ace10dee7b360002a9d8ffffffffbd3260ebg
        
        try {
            company_id = Long.parseLong(url_string.substring(0,8), 16);
            
            // System.out.println("new hash");
            mailing_id = to_decimal_new(url_string.substring(8,16));
            customer_id = to_decimal_new(url_string.substring(16,24));
            parameter = to_decimal_new(url_string.substring(24,32));
            hash_value2 = url_string.substring(32);
            check_hash2 = get_new_hashcode(customer_id);
System.err.println("CustomerID: "+customer_id);
        } catch (Exception e) {
            System.out.println("ex auto: "+e);
            return false;
        }
        
        if(hash_value2.compareTo(check_hash2)!=0){
            System.out.println("wrong hash auto: "+hash_value2+" "+check_hash2);
            return false;
        }
        
        return true;
    }
    
    
    //
    // encrypt string
    // - presumes that commpany id and mailing id were set correctly in constructor
    //
    public String encrypt(long customer_id)throws Exception{
        
        this.parameter=0;
        String customer_crypt = to_hex_new(customer_id, 8);
        String hash_value = get_new_hashcode(customer_id);
        
        return  mailing_clear + company_crypt + customer_crypt + hash_value + "g";
    }
    
    //
    // encrypt auto url
    // - just insert the parameter before the hashcode
    //
    public String encrypt_autourl(long customer_id, long parameter)throws Exception{
        
        String customer_crypt = to_hex_new(customer_id, 8);
        String parameter_crypt = to_hex_new(parameter, 8);
        this.parameter=parameter;
        // long hashcode=get_new_hashcode(customer_id+parameter);
        String hash_value = get_new_hashcode(customer_id+parameter);
        
        return  mailing_clear + company_crypt + customer_crypt +
        parameter_crypt + hash_value + "g";
    }

        //
    // encrypt auto deeptracking
    // - just insert the parameter before the hashcode
    //
    public String encryptByCompanyID(long customer_id, long parameter) throws Exception {
        
        String customer_crypt = to_hex_new(customer_id, 8);
        String parameter_crypt = to_hex_new(parameter, 8);
        String mailing_crypt = to_hex_new(mailing_id, 8);
        String company_clear = add_zeros(Long.toHexString(company_id), 8);
        // long hashcode=get_new_hashcode(customer_id+parameter);
        String hash_value = get_new_hashcode(customer_id);
        
        return  company_clear + mailing_crypt + customer_crypt +
        parameter_crypt + hash_value + "gc";
    }

    
    //
    // test method
    public static void main(String args[]){
        
        // encrypt:
        try{
            TagString tester1=new TagString(568565, 20,16223); // comp, mailing
            
            String test1 = tester1.encrypt_autourl(233232323,656685); // cust id, parameter
            System.out.println("URL-String: " + test1);
            
            // decrypt:
            TagString reader = new TagString();
            // reader.set_xor_key(568565);
            if(reader.decrypt_autourl(test1, 568565)){ // egal, string
                System.out.println("True auto!");
            }
            System.out.println(reader.toString());
            
            
            // normal tag:
            //
            // encrypt:
            TagString tester2=new TagString(568565, 20,16223); // comp, mailing
            
            String test2 = tester2.encrypt(233232323); // customer id
            System.out.println("URL-String: " + test2);
            
            // decrypt:
            TagString reader2 = new TagString();
            if(reader2.decrypt(test2, 568565)){
                System.out.println("True normal!");
            }
            System.out.println(reader2.toString());
            
            //reader2.crypt();
            //reader.crypt();
            
            //System.out.println(reader2.make_profile_url(2332));
            //System.out.println(reader2.get_mailing_id(test2));
            //System.out.println(reader.make_autourl(2332, 66));
            //System.out.println(reader2.make_unsubscribe_url(2332));
            
        } catch(Exception e){
            System.err.println("Error: " + e );
        }
        
    } // end method
    
    public boolean decryptFromString(String param, Connection dbConn) {
        String sqlGetKey=null;
        Statement agnStatement=null;
        ResultSet rset=null;
        boolean result=false;
        
        if(param.endsWith("&") || param.endsWith("S")) {
            sqlGetKey="SELECT XOR_KEY FROM MAILINGLIST_TBL WHERE MAILINGLIST_ID=" + this.get_mailing_id(param) + " AND IS_PUBLIC=1";
            param=param.substring(0, param.length()-1);
        } else {
            sqlGetKey="SELECT XOR_KEY FROM MAILING_TBL WHERE MAILING_ID=" + this.get_mailing_id(param);
        }
        
        try {
            agnStatement=dbConn.createStatement();
            rset=agnStatement.executeQuery(sqlGetKey);
            
            if(rset.next()) {
                this.XOR_KEY=rset.getLong(1);
            }
            
        } catch (Exception e) {
            System.out.println("decodeFromString-Error: " + e);
        }
        
        try {
            rset.close();
        } catch (Exception e) {
            System.out.println("decodeFromString-Error: " + e);
        }
        
        try {
            agnStatement.close();
        } catch (Exception e) {
            System.out.println("decodeFromString-Error: " + e);
        }
        
        try {
            result=this.decrypt(param);
        } catch (Exception e2) {
            System.out.println("decode prob: "+e2);
            result=false;
        }
        
        return result;
    }
    
}// end class

