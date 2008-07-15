/*
 * Ruler.java
 *
 * Created on 23. Mai 2006, 14:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.agnitas.backend;

/**
 *
 * @author mhe
 */
public interface Ruler {
    /**
     * Cleanup
     */
    void done() throws Exception;
    
    /** Wrapper for kickOff to be used in Quartz scheduler
     */
    void kickOffSimple();
    
    /**
     * Loop over all entries for today and start the
     * mailings, which are ready to run
     */
    String kickOff() throws Exception;
    
    /**
     * Setter for hour
     * @param nhour the new hour to send mailings for
     */
    void setHour(int nhour);
    
    /**
     * Start delayed mail generation
     */
    public void kickOffDelayed();
}
