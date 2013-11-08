package org.agnitas.beans.impl;

import org.agnitas.beans.EmmLayoutBase;

public class EmmLayoutBaseImpl implements EmmLayoutBase {
	private int id;
	private String baseURL;
	private String imagesURL;
	private String cssURL;
	private String jsURL;
    private int menuPosition = MENU_POSITION_DEFAULT;
	
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

}
