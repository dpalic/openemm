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
package org.agnitas.exceptions;

/**
 * @author: Igor
 */
public class EncodingError {
    private String strWithError;
    private int line;
    private int column;
    private char invalidChar;

    public EncodingError(String strWithError, int line, int column){
        this(strWithError, line, column, '\0');
    }

    public EncodingError(String strWithError, int line, int column, char invalidChar){
           this.strWithError = strWithError;
           this.line = line;
           this.column = column;
           this.invalidChar = invalidChar;
    }

    public String getStrWithError() {
        return strWithError;
    }

    public void setStrWithError(String strWithError) {
        this.strWithError = strWithError;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public char getInvalidChar() {
        return invalidChar;
    }

    public void setInvalidChar(char invalidChar) {
        this.invalidChar = invalidChar;
    }
}
