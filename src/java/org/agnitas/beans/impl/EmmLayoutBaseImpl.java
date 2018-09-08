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
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.beans.impl;

import org.agnitas.beans.EmmLayoutBase;

public class EmmLayoutBaseImpl implements EmmLayoutBase {
	private int id;
	private String baseURL;
	private String imagesURL;
	private String cssURL;
	private String jsURL;
	private String shortname;
    private int menuPosition = MENU_POSITION_DEFAULT;
    private int livepreviewPosition = LIVEPREVIEW_POSITION_RIGHT;

	public EmmLayoutBaseImpl(int id, String baseUrl) {
		this.id = id;
		this.baseURL = baseUrl;
		this.imagesURL = baseUrl + "/images";
		this.cssURL = baseUrl + "/styles";
		this.jsURL = baseUrl + "/js";
	}
	/* (non-Javadoc)
	 * @see org.agnitas.beans.EmmBaseLayout#getId()
	 */
	public int getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see org.agnitas.beans.EmmBaseLayout#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see org.agnitas.beans.EmmBaseLayout#getBaseURL()
	 */
	public String getBaseURL() {
		return baseURL;
	}
	/* (non-Javadoc)
	 * @see org.agnitas.beans.EmmBaseLayout#setBaseURL(java.lang.String)
	 */
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}
	/* (non-Javadoc)
	 * @see org.agnitas.beans.EmmBaseLayout#getImageURL()
	 */
	public String getImagesURL() {
		return imagesURL;
	}
	/* (non-Javadoc)
	 * @see org.agnitas.beans.EmmBaseLayout#setImageURL(java.lang.String)
	 */
	public void setImagesURL(String imageURL) {
		this.imagesURL = imageURL;
	}
	/* (non-Javadoc)
	 * @see org.agnitas.beans.EmmBaseLayout#getCssURL()
	 */
	public String getCssURL() {
		return cssURL;
	}
	/* (non-Javadoc)
	 * @see org.agnitas.beans.EmmBaseLayout#setCssURL(java.lang.String)
	 */
	public void setCssURL(String cssURL) {
		this.cssURL = cssURL;
	}
	/* (non-Javadoc)
	 * @see org.agnitas.beans.EmmBaseLayout#getJsURL()
	 */
	public String getJsURL() {
		return jsURL;
	}
	/* (non-Javadoc)
	 * @see org.agnitas.beans.EmmBaseLayout#setJsURL(java.lang.String)
	 */
	public void setJsURL(String jsURL) {
		this.jsURL = jsURL;
	}

    public int getMenuPosition() {
        return menuPosition;
    }

    public void setMenuPosition(int menuPosition) {
       this.menuPosition = menuPosition;
    }

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

    public int getLivepreviewPosition() {
        return livepreviewPosition;
    }

    public void setLivepreviewPosition(int livepreviewPosition) {
        this.livepreviewPosition = livepreviewPosition;
    }
}
