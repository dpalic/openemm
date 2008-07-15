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

package org.agnitas.web;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.font.*;
import org.agnitas.util.*;
import org.agnitas.beans.EmmLayout;
import javax.imageio.*;
import org.springframework.context.*;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ButtonRender extends HttpServlet {
    
    protected Font ttfFontS=null;
    protected Font ttfFontNN=null;
    protected Font ttfFontNH=null;
    protected String realPath=null;
    protected TimeoutLRUMap buttonCache=null;
    
    /**
     * Initialization.
     */
    public void init(ServletConfig config) throws ServletException {
        ServletContext aContext=config.getServletContext();
        
        buttonCache=new TimeoutLRUMap(500, 30000); // 500 entrys / 5000 ms
        
        // -Djava.awt.headless=true should be set in startup-script
        System.setProperty("java.awt.headless", "true");
        AgnUtils.logger().info("init: JDK "+System.getProperty("java.version"));
        
        try {
            ttfFontS=new Font("Tahoma", Font.BOLD, 14);
            ttfFontNN=new Font("Tahoma", Font.PLAIN, 18);
            ttfFontNH=new Font("Tahoma", Font.BOLD, 18);
        } catch (Exception e) {
            AgnUtils.logger().error("init: "+e.getMessage());
        }
        
        try {
            realPath=aContext.getRealPath("/");
        } catch (Exception e) {
            AgnUtils.logger().error("init: "+e.getMessage());
        }
        super.init(config);
    }
    
    /**
     * Draws the buttons.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse response)
    throws IOException, ServletException {
        
        ServletOutputStream out=null;
        int buttonType=0;
        Image baseImage=null;
        BufferedImage image=null;
        Font theFont=null;
        Graphics2D g=null;
        EmmLayout aLayout=null;
        double yPos=-1.0;
        double xPos=-1.0;
        
        if(req.getParameter("msg")==null) {
            AgnUtils.logger().info("doGet: no message");
            return;
        }
        
        if(req.getParameter("lm")!=null) {
            try {
                xPos=Double.parseDouble(req.getParameter("lm"));
            } catch (Exception e) {
                xPos=-1.0;
            }
        }
        
        try {
            buttonType=Integer.parseInt(req.getParameter("t"));
        } catch (Exception e) {
            buttonType=0; // Default
        }
       
        if(req.getSession().getAttribute("emm.layout")!=null) {
            aLayout=(EmmLayout)req.getSession().getAttribute("emm.layout");
        } else {
            if(req.getAttribute("emm.layout")!=null) {
                aLayout=(EmmLayout)req.getAttribute("emm.layout");
            } else {
                ApplicationContext aContext=WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
                aLayout=(EmmLayout)aContext.getBean("EmmLayout");
            }
        }
        
        String localestring=new String("");
        Locale aLoc=null;
        if(req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY)!=null) {
            aLoc=(Locale)req.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
            localestring=aLoc.toString();
        } else {
            localestring=req.getLocale().toString();
            aLoc=req.getLocale();
        }
        
        String cacheKey=req.getParameter("msg")+"_"+xPos+"_"+buttonType+"_"+aLayout.getLayoutID()+"_"+localestring;
        ButtonImage theImage=(ButtonImage)this.buttonCache.get(cacheKey);
        
        if(theImage==null) {
            String message=SafeString.getLocaleString(req.getParameter("msg"), aLoc);
                        
            switch(buttonType) {
                case 1:
                    try {
                        baseImage=ImageIO.read(new File(realPath+aLayout.getBaseUrl()+"button_nn.gif"));
                    } catch (Exception e) {
                        AgnUtils.logger().error("doGet: "+e.getMessage());
                    }
                    image = new BufferedImage(baseImage.getWidth(null), baseImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                    g=image.createGraphics();
                    theFont=ttfFontNN;
                    g.setColor(Color.black);
                    break;
                    
                case 2:
                    try {
                        baseImage=ImageIO.read(new File(realPath+aLayout.getBaseUrl()+"button_nh.gif"));
                    } catch (Exception e) {
                        AgnUtils.logger().error("doGet: "+e.getMessage());
                    }
                    image = new BufferedImage(baseImage.getWidth(null), baseImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                    g=image.createGraphics();
                    theFont=ttfFontNH;
                    g.setColor(Color.white);
                    break;
                    
                default:
                    try {
                        System.err.println("Pfad: " + realPath+aLayout.getBaseUrl()+"button_s.gif");
                        baseImage=ImageIO.read(new File(realPath+aLayout.getBaseUrl()+"button_s.gif"));
                    } catch (Exception e) {
                        AgnUtils.logger().error("doGet: "+e.getMessage());
                    }
                    image = new BufferedImage(baseImage.getWidth(null), baseImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                    g=image.createGraphics();
                    theFont=ttfFontS;
                    g.setColor(Color.black);
                    break;
            }
            
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.drawImage(baseImage, null, null);
                g.setFont(theFont);
                FontMetrics aMetrics=g.getFontMetrics(theFont);
                LineMetrics aLine=aMetrics.getLineMetrics(message, g);
                
                yPos=(baseImage.getHeight(null)/2)+((aLine.getAscent()+aLine.getDescent())/2);
                yPos=yPos-(aLine.getDescent());
                if(xPos==-1.0) {
                    xPos=(baseImage.getWidth(null)-aMetrics.getStringBounds(message, g).getWidth())/2;
                }
                g.drawString(message, (int)xPos, (int)yPos);
            // Send image to the web browser
            theImage=new ButtonImage();
            ByteArrayOutputStream aBOut=new ByteArrayOutputStream();
            ImageIO.write(image, "png", aBOut);
            theImage.imageData=aBOut.toByteArray();
            this.buttonCache.put(cacheKey, theImage);
        }
        
        response.setContentType("image/png");  // Assign correct content-type
        ServletOutputStream aOut=response.getOutputStream();
        aOut.write(theImage.imageData);
    }
    
    private class ButtonImage {
        public byte[] imageData;
    }
}
