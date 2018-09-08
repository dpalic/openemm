package org.agnitas.emm.core.action.operations;

public class ActionOperationUpdateCustomer extends AbstractActionOperation {
    
	private static final long serialVersionUID = 1L;

    public static final int TYPE_INCREMENT_BY = 1;
    public static final int TYPE_DECREMENT_BY = 2;
    public static final int TYPE_SET_VALUE = 3;
	
	private String columnName;
	private int updateType;
	private String updateValue;
	private String columnType;

	public ActionOperationUpdateCustomer(String type) {
		super(type);
	}
	
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getUpdateType() {
		return updateType;
	}

	public void setUpdateType(int updateType) {
		this.updateType = updateType;
	}

	public String getUpdateValue() {
		return updateValue;
	}

	public void setUpdateValue(String updateValue) {
		this.updateValue = updateValue;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

    /**
     * Getter for property nameType.
     *
     * @return Value of property nameType.
     */
    public String getNameType() {
        return this.columnName + "#" + this.columnType;
    }
    
    /**
     * Setter for property nameType.
     *
     * @param nameType New value of property nameType.
     */
    public void setNameType(String nameType) {
        this.columnType = nameType.substring(nameType.indexOf('#')+1);
        this.columnName = nameType.substring(0, nameType.indexOf('#'));
    }

}
