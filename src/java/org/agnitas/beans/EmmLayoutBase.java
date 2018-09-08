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
package org.agnitas.beans;

import java.io.Serializable;

public interface EmmLayoutBase extends Serializable {

    public static final int MENU_POSITION_LEFT = 0;
    public static final int MENU_POSITION_TOP = 1;
    public static final int MENU_POSITION_DEFAULT = MENU_POSITION_LEFT;

    public static final int LIVEPREVIEW_POSITION_RIGHT = 0;
    public static final int LIVEPREVIEW_POSITION_BOTTOM = 1;
    public static final int LIVEPREVIEW_POSITION_DEACTIVATE = 2;


	/**
	 * @return the id
	 */
	public int getId();

	/**
	 * @param id the id to set
	 */
	public void setId(int id);

	/**
	 * @return the baseURL
	 */
	public String getBaseURL();

	/**
	 * @param baseURL the baseURL to set
	 */
	public void setBaseURL(String baseURL);

	/**
	 * @return the imagesURL
	 */
	public String getImagesURL();

	/**
	 * @param imagesURL the imagesURL to set
	 */
	public void setImagesURL(String imagesURL);

	/**
	 * @return the cssURL
	 */
	public String getCssURL();

	/**
	 * @param cssURL the cssURL to set
	 */
	public void setCssURL(String cssURL);

	/**
	 * @return the jsURL
	 */
	public String getJsURL();

	/**
	 * @param jsURL the jsURL to set
	 */
	public void setJsURL(String jsURL);

	/**
	 * @return the menuPosition
	 */
	public int getMenuPosition();

	/**
	 * @param menuPosition the menuPosition to set
	 */
	public void setMenuPosition(int menuPosition);

    /**
	 * @return the livepreviewPosition
	 */
    public int getLivepreviewPosition();

    /**
	 * @param livepreviewPosition the livepreviewPosition to set
	 */
    public void setLivepreviewPosition(int livepreviewPosition);

}