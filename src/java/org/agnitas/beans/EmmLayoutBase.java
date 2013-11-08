package org.agnitas.beans;

import java.io.Serializable;

public interface EmmLayoutBase extends Serializable {

    public static final int MENU_POSITION_LEFT = 0;
    public static final int MENU_POSITION_TOP = 1;
    public static final int MENU_POSITION_DEFAULT = MENU_POSITION_LEFT;

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
	 * @param imageURL the imagesURL to set
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

}