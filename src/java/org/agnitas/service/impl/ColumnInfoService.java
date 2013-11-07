package org.agnitas.service.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.agnitas.beans.ProfileField;
import org.agnitas.dao.ColumnInfoDao;
import org.agnitas.dao.ProfileFieldDao;
import org.agnitas.dao.impl.ColumnInfoDaoImpl;

public class ColumnInfoService implements  org.agnitas.service.ColumnInfoService {

	private ColumnInfoDao columnInfoDao;
	private ProfileFieldDao profileFieldDao;
	
	public Map getColumnInfo(int companyID, String column) throws Exception {
	
	LinkedHashMap<String,Hashtable<String,Object>> list= (LinkedHashMap<String, Hashtable<String, Object>>) ((ColumnInfoDaoImpl) columnInfoDao).getColumnInfo(companyID, column) ;
	if(companyID <= 0) {
    	return list;
	}    	
	LinkedHashMap<String,Map<String,Object>> nlist=new LinkedHashMap<String, Map<String,Object>>();
	try	{
		
		Iterator	i=list.keySet().iterator();
		while(i.hasNext()) {
			String	key=(String) i.next();
			Map	m=(Map) list.get(key);
			String	col=(String) m.get("column");
            ProfileField field=profileFieldDao.getProfileField(companyID, col);

			if(field != null) {
				m.put("shortname", field.getShortname());
				m.put("default", field.getDefaultValue());
				m.put("description", field.getDescription());
				m.put("editable", new Integer(field.getModeEdit()));
				m.put("insertable", new Integer(field.getModeInsert()));
			}
			nlist.put((String)m.get("column"), m);
		}			
        } catch(Exception e) {
            throw e;
        }
        // sort the columnlist by the shortname
        LinkedHashMap<String, Map<String, Object>> sortedList = sortColumnListByShortName(nlist);
        return sortedList;
	}
	
	protected static LinkedHashMap<String, Map<String, Object>> sortColumnListByShortName(
			LinkedHashMap<String, Map<String, Object>> nlist) {
		LinkedHashMap<String,Map<String,Object>> sortedList = new LinkedHashMap<String, Map<String,Object>>();
        Map.Entry<String,Map<String,Object>>[]  nlistEntries = nlist.entrySet().toArray(new Map.Entry[0]);
        Arrays.sort(nlistEntries, new Comparator<Map.Entry>() {

			public int compare(Entry entry1, Entry entry2) {
				String shortname1 = ((String) ((Map)entry1.getValue()).get("shortname")).toLowerCase();
				String shortname2 = ((String) ((Map)entry2.getValue()).get("shortname")).toLowerCase();
				return  shortname1.compareTo(shortname2);
			}
			
		});
		
		for (Entry<String, Map<String, Object>> entry : nlistEntries) {
			sortedList.put(entry.getKey(),entry.getValue());
		}
		return sortedList;
	}
	
	

	public void setColumnInfoDao(ColumnInfoDao columnInfoDao) {
		this.columnInfoDao = columnInfoDao;
	}

	public void setProfileFieldDao(ProfileFieldDao profileFieldDao) {
		this.profileFieldDao = profileFieldDao;
	}

}
