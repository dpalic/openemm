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
package org.agnitas.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.agnitas.dao.DocMappingDao;
import org.springframework.jdbc.core.JdbcTemplate;

public class DocMappingDaoImpl implements DocMappingDao{
    private DataSource dataSource;

    @Override
    public String getHelpFilename(String helpKey) {
        return null;
    }

    @Override
    public Map<String,String> getDocMapping(){
        JdbcTemplate template = new JdbcTemplate(dataSource);
        String sql = "select pagekey, filename from doc_mapping_tbl";
        List<Map<String,Object>> tmpList = template.queryForList(sql);
        HashMap<String, String> result = new HashMap<String, String>();
        if(tmpList != null){
           for(Map<String,Object> map : tmpList){
               String pagekey = (String) map.get("pagekey");
               String filename = (String) map.get("filename");
               result.put(pagekey,filename);
           }
        }
        return result;
    }

    public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
