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
 * the code written by AGNITAS AG are Copyright (c) 2009 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/

package org.agnitas.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Comparator for sorting text-modules in content tab.
 * Compares dynTag names as usual Strings but number values in Strings
 * are compared like int values
 *
 * @author Vyacheslav Stepanov
 */
public class DynTagNameComparator implements Comparator {

    /**
     * Compares two names of dynTags as usual Strings but
     * number values in names are compared like int values
     *
     * @param name1 first name
     * @param name2 second name
     * @return -1 if name1 is lesser; 0 if names are equal; 1 if name1 is greater.
     */
    public int compare(Object name1, Object name2) {
        String firstName = (String) name1;
        String secondName = (String) name2;
        if(firstName.equals(secondName)) {
            return 0;
        }
        List<String> firstNameTokens = splitNumbersAndText(firstName);
        List<String> secondNameTokens = splitNumbersAndText(secondName);
        int tokensNum = Math.min(firstNameTokens.size(), secondNameTokens.size());
        for(int i = 0; i < tokensNum; i++) {
            String firstToken = firstNameTokens.get(i);
            String secondToken = secondNameTokens.get(i);
            if(firstToken.equals(secondToken)) {
                continue;
            }
            if(isNumber(firstToken) && isNumber(secondToken)) {
                int firstNumber = Integer.parseInt(firstToken);
                int secondNumber = Integer.parseInt(secondToken);
                return firstNumber < secondNumber ? -1 : 1;
            } else {
                return firstName.compareToIgnoreCase(secondName);
            }
        }
        return firstName.compareToIgnoreCase(secondName);
    }

    /**
     * Splits String into a list of strings separating text values from number values
     * Example: "abcd 23.56 ueyr76" will be split to "abcd ", "23", ".", "56", " ueyr", "76"
     *
     * @param str string to split
     * @return split-list of strings
     */
    private List<String> splitNumbersAndText(String str) {
        List<String> tokens = new ArrayList<String>();
        int tokenStart = 0;
        boolean isSequenceNumber = isNumber(str.substring(0, 1));
        for(int i = 1; i < str.length(); i++) {
            boolean isCurrentNumber = isNumber(str.substring(i, i + 1));
            if(i > tokenStart && isCurrentNumber != isSequenceNumber) {
                tokens.add(str.substring(tokenStart, i));
                tokenStart = i;
            }
            isSequenceNumber = isCurrentNumber;
        }
        tokens.add(str.substring(tokenStart, str.length()));
        return tokens;
    }

    /**
     * Checks if String contains only digit characters
     *
     * @param str string for check
     * @return true if String contains only digit characters, false otherwise
     */
    private boolean isNumber(String str) {
        for(int i = 0; i < str.length(); i++) {
            if(!(str.charAt(i) >= '0' && str.charAt(i) <= '9')) {
                return false;
            }
        }
        return true;
    }

}
