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
package org.agnitas.preview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.agnitas.beans.TagDefinition;
import org.agnitas.dao.TagDao;
import org.agnitas.preview.AgnTagError.AgnTagErrorKey;
import org.agnitas.util.AgnUtils;
import org.agnitas.util.CsvDataException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * The Class TagSyntaxChecker.
 */
public class TagSyntaxChecker {
	/**
	 * Currently it is allowed to use simple values for parametervalues without quotes.
	 * If this is not wanted anymore change this value.
	 */
	private static boolean ALLOW_UNQUOTED_PARAMETER_VALUES = true;
	
	/**
	 * Any agnTag may have a closing slash, this is not defined by now.
	 * In a future implementation any standalone agnTags should have a closing slash analog to XML-Tags and only opening agnDYN-Tags should look like an opening XML-Tag without any slashes.
	 */
	private static boolean ALLOW_LEGACY_MISSING_CLOSING_SLASHES = true;
	
	
	/** The tag dao. */
	private TagDao tagDao;

	/**
	 * Sets the tag dao.
	 *
	 * @param tagDao the new tag dao
	 */
	@Required
	public void setTagDao(TagDao tagDao) {
		this.tagDao = tagDao;
	}

    /**
     * Check a component text for agnTag syntax validity
     *
     * @param companyID the company id
     * @param contentText the content text
     * @return true, if successful
     * @throws Exception the exception
     */
    public List<AgnTagError> check(int companyID, String contentText) throws Exception {
    	List<AgnTagError> returnList = new ArrayList<AgnTagError>();
    	check(companyID, contentText, returnList);
    	return returnList;
    }
	
    /**
     * Check a component text for agnTag syntax validity
     *
     * @param companyID the company id
     * @param contentText the content text
     * @param agnTagSyntaxErrors the agn tag syntax errors
     * @return true, if successful
     * @throws Exception the exception
     */
    public boolean check(int companyID, String contentText, List<AgnTagError> agnTagSyntaxErrors) throws Exception {
    	try {
			String tagStartText = "[agn";
			String closingTagStartText = "[/agn";
			String tagEndText = "]";
			char tagNameSeparator = ' ';
			String trailingCloserSign = "/"; // used for [agnTag name="xxx"/]
			String closingTagSign = "/"; // only allowed for agnDYN-Tags: [/agnDYN name="xxx"]
			int searchIndex = 0;
			boolean errorsFoundGlobal = false;
			boolean errorsFoundInCurrentTag;
			Stack<String> openAgnDynTags = new Stack<String>();
			
			Map<String, TagDefinition> tagDefinitions = tagDao.getTagDefinitionsMap(companyID);
			
			while ((searchIndex = AgnUtils.searchNext(contentText, searchIndex, tagStartText, closingTagStartText)) >= 0) {
				errorsFoundInCurrentTag = false;
				
				// Search the border brackets of this tag
				int tagStartIndex = searchIndex;
				int tagEndIndex = contentText.indexOf(tagEndText, tagStartIndex + tagStartText.length());
				if (tagEndIndex < 0) {
					// No end bracket was found. Try to find the end of this tags name for error text
					Matcher nextNonWordMathcher = Pattern.compile("[\\W]").matcher(contentText);
					boolean foundNonWord = nextNonWordMathcher.find(tagStartIndex + tagStartText.length());
					String maybeTagName;
					if (foundNonWord) {
						maybeTagName = contentText.substring(tagStartIndex, nextNonWordMathcher.start());
					} else {
						maybeTagName = contentText.substring(tagStartIndex);
					}
					agnTagSyntaxErrors.add(new AgnTagError(maybeTagName.substring(1), maybeTagName, AgnTagErrorKey.missingClosingBracket, contentText, tagStartIndex));
					errorsFoundInCurrentTag = true;
					
					searchIndex++;
				} else {
					String fullTagText = contentText.substring(tagStartIndex, tagEndIndex + 1);
					
					// Search for the end of the tag name
					int tagNameEndIndex = tagEndIndex;
					int tagNameSeparatorIndex = contentText.indexOf(tagNameSeparator, tagStartIndex + tagStartText.length());
					if (tagNameSeparatorIndex > 0 && tagNameSeparatorIndex < tagEndIndex) {
						tagNameEndIndex = tagNameSeparatorIndex;
					}

					String tagName = contentText.substring(tagStartIndex + 1, tagNameEndIndex);
					// tag is like [agnTag name="xxx"/]
					boolean isClosingTag = false;
					if (tagName.startsWith(trailingCloserSign)) {
						tagName = tagName.substring(trailingCloserSign.length());
						isClosingTag = true;
					}

					boolean hasTrailingCloserSign = false;
					Map<String, String> tagParameterMap = new HashMap<String, String>();
					
					if (!errorsFoundInCurrentTag && tagNameEndIndex < tagEndIndex) {
						// Read tag parameters
						String tagParameterString = contentText.substring(tagNameEndIndex + 1, tagEndIndex);
						// tag is like [/agnTag name="xxx"]
						if (tagParameterString.endsWith(closingTagSign)) {
							tagParameterString = tagParameterString.substring(0, tagParameterString.length() - closingTagSign.length());
							hasTrailingCloserSign = true;
						}
						
						if (tagNameEndIndex + 1 < tagEndIndex) {
							// Read all parameters
							try {
								tagParameterMap = readTagParameterString(tagParameterString);
							} catch (AgnTagError ate) {
								ate.setTagName(tagName);
								ate.setFullAgnTagText(fullTagText);
								ate.setTextPosition(contentText, tagStartIndex);
								agnTagSyntaxErrors.add(ate);
								errorsFoundInCurrentTag = true;
							} catch (Exception e) {
								agnTagSyntaxErrors.add(new AgnTagError(tagName, fullTagText, AgnTagErrorKey.invalidParameterSyntax, contentText, tagStartIndex, e.getMessage()));
								errorsFoundInCurrentTag = true;
							}
						}
					}

					if (!errorsFoundInCurrentTag) {
						// Check tag name
						if (!tagDefinitions.containsKey(tagName)) {
							agnTagSyntaxErrors.add(new AgnTagError(tagName, fullTagText, AgnTagErrorKey.unknownAgnTag, contentText, tagStartIndex));
							errorsFoundInCurrentTag = true;
						}
					}

					if (!errorsFoundInCurrentTag) {
						// Check tag definitions
						if (isClosingTag && hasTrailingCloserSign) {
							// tag is like [/agnTag name="xxx"/]
							agnTagSyntaxErrors.add(new AgnTagError(tagName, fullTagText, AgnTagErrorKey.invalidAgnTagSlashes, contentText, tagStartIndex));
							errorsFoundInCurrentTag = true;
						} else if (!"agnDYN".equals(tagName) && isClosingTag) {
							// Only agnDYN-Tag may start with slash as a closing tag
							agnTagSyntaxErrors.add(new AgnTagError(tagName, fullTagText, AgnTagErrorKey.invalidClosingAgnTag, contentText, tagStartIndex));
							errorsFoundInCurrentTag = true;
						} else if (!"agnDYN".equals(tagName) && !hasTrailingCloserSign && ! ALLOW_LEGACY_MISSING_CLOSING_SLASHES) {
							// Any agnTag may have a closing slash, this is not defined by now.
							// In a future implementation any standalone agnTags should have a closing slash analog to XML-Tags and only opening agnDYN-Tags should look like an opening XML-Tag without any slashes.
							agnTagSyntaxErrors.add(new AgnTagError(tagName, fullTagText, AgnTagErrorKey.missingAgnTagClosingSlash, contentText, tagStartIndex));
							errorsFoundInCurrentTag = true;
						} else {
							for (String mandatoryParameterName : tagDefinitions.get(tagName).getMandatoryParameters()) {
								if (!tagParameterMap.containsKey(mandatoryParameterName)) {
									agnTagSyntaxErrors.add(new AgnTagError(tagName, fullTagText, AgnTagErrorKey.missingParameter,contentText, tagStartIndex, mandatoryParameterName));
									errorsFoundInCurrentTag = true;
								}
							}
						}
					}
					
					if ("agnDYN".equals(tagName)) {
						// Check for order of agnDYN-Tags, which can be opened and closed
						if (isClosingTag) {
							if (openAgnDynTags.size() == 0) {
								agnTagSyntaxErrors.add(new AgnTagError(tagName, fullTagText, AgnTagErrorKey.invalidClosingAgnDynTag_notOpened, contentText, tagStartIndex));
								errorsFoundInCurrentTag = true;
							} else if (openAgnDynTags.peek().equals(tagParameterMap.get("name"))) {
								openAgnDynTags.pop();
							} else {
								agnTagSyntaxErrors.add(new AgnTagError(tagName, fullTagText, AgnTagErrorKey.invalidClosingAgnDynTag_notMatchingLastOpenedName, contentText, tagStartIndex, openAgnDynTags.peek()));
								errorsFoundInCurrentTag = true;
							}
						} else if (!hasTrailingCloserSign) {
							// Opening agnDYN-Tag
							if (tagParameterMap.get("name") != null) {
								openAgnDynTags.push(tagParameterMap.get("name"));
							}
						}
					} else if ("agnDVALUE".equals(tagName)) {
						// Check for enclosing of agnDVALUE-Tags, which may only be used within the matching agnDYN-Tags
						if (!openAgnDynTags.contains(tagParameterMap.get("name"))) {
							agnTagSyntaxErrors.add(new AgnTagError(tagName, fullTagText, AgnTagErrorKey.unwrappedAgnDvalueTag, contentText, tagStartIndex, tagParameterMap.get("name")));
							errorsFoundInCurrentTag = true;
						}
					}
					
					searchIndex = tagEndIndex;
				}
				
				errorsFoundGlobal = errorsFoundGlobal || errorsFoundInCurrentTag;
			}
			
			if (openAgnDynTags.size() > 0) {
				agnTagSyntaxErrors.add(new AgnTagError("agnDYN", "[agnDYN name=\"" + openAgnDynTags.peek() + "\"]", AgnTagErrorKey.missingClosingAgnDynTag, openAgnDynTags.peek()));
				errorsFoundGlobal = true;
			}
			
			return !errorsFoundGlobal;
		} catch (Exception e) {
			throw new Exception("Error in agn-Tags: " + e.getMessage(), e);
		}
    }
    
    /**
     * Read tag parameter string.
     *
     * @param agnTagParameterString the agn tag parameter string
     * @return the map
     * @throws Exception the exception
     */
    public static Map<String, String> readTagParameterString(String agnTagParameterString) throws Exception {
		Map<String, String> returnMap = new HashMap<String, String>();
		
		if (StringUtils.isEmpty(agnTagParameterString)) {
			return returnMap;
		}
		
		StringBuilder nextKey = new StringBuilder();
		StringBuilder nextValue = new StringBuilder();
		boolean readingKey = true;
		boolean insideString = false;
		char stringQuote = '"';
		char entrySeparator = ' ';
		char keyValueSeparator = '=';
		
		for (int i = 0; i < agnTagParameterString.length(); i++) {
			char nextChar = agnTagParameterString.charAt(i);
			if (nextChar == '\t' || nextChar == '\r' || nextChar == '\n' || (nextChar == ' ' && i - 1 >= 0 && agnTagParameterString.charAt(i - 1) == ' ')) {
				// whitespaces are not allowed when not inside of a value string or near the equal sign
				throw new AgnTagError(AgnTagErrorKey.invalidWhitespace);
			} else if (nextChar == stringQuote) {
				if (readingKey) {
					throw new AgnTagError(AgnTagErrorKey.invalidQuotedKey);
				} else if (!insideString && nextValue.length() > 0) {
					throw new AgnTagError(AgnTagErrorKey.invalidUnquotedValue);
				} else {
					nextValue.append(nextChar);
					insideString = !insideString;
				}
			} else if (!insideString) {
				if (nextChar == entrySeparator) {
					if (nextKey.length() == 0) {
						throw new AgnTagError(AgnTagErrorKey.invalidEmptyKey);
					} else {
						// Add entry
						if (returnMap.containsKey(nextKey.toString())) {
							throw new AgnTagError(AgnTagErrorKey.duplicateKey);
						}
						returnMap.put(nextKey.toString(), parseValueDataString(nextValue.toString(), stringQuote));
						nextKey = new StringBuilder();
						nextValue = new StringBuilder();
						readingKey = true;
					}
				} else if (nextChar == keyValueSeparator) {
					readingKey = false;
				} else {
					if (readingKey) {
						nextKey.append(nextChar);
					} else {
						if (ALLOW_UNQUOTED_PARAMETER_VALUES) {
							nextValue.append(nextChar);
						} else {
							throw new AgnTagError(AgnTagErrorKey.invalidUnquotedValue);
						}
					}
				}
			} else { // insideString
				if (readingKey) {
					nextKey.append(nextChar);
				} else {
					nextValue.append(nextChar);
				}
			}
		}
		
		if (insideString) {
			throw new AgnTagError(AgnTagErrorKey.unexpectedEndOfValue);
		} else if (readingKey) {
			throw new AgnTagError(AgnTagErrorKey.unexpectedEndOfKey);
		} else {
			if (nextKey.length() == 0) {
				throw new AgnTagError(AgnTagErrorKey.invalidEmptyKey);
			} else {
				// Add last entry
				if (returnMap.containsKey(nextKey.toString())) {
					throw new AgnTagError(AgnTagErrorKey.duplicateKey);
				}
				returnMap.put(nextKey.toString(), parseValueDataString(nextValue.toString(), stringQuote));
				return returnMap;
			}
		}
	}
	
	/**
	 * Remove stringQuotes at start and end and unescape double stringQuotes within the resulting string.
	 *
	 * @param rawValue the raw value
	 * @param stringQuote the string quote
	 * @return the string
	 * @throws CsvDataException the csv data exception
	 */
	private static String parseValueDataString(String rawValue, char stringQuote) {
		String returnValue = rawValue;
		String stringQuoteString = Character.toString(stringQuote);
		
		if (StringUtils.isNotEmpty(rawValue)) {
			if (returnValue.charAt(0) == stringQuote && returnValue.charAt(returnValue.length() - 1) == stringQuote) {
				returnValue = returnValue.substring(1, returnValue.length() - 1);
				returnValue = returnValue.replace(stringQuoteString + stringQuoteString, stringQuoteString);
			}
			returnValue = returnValue.replace("\r\n", "\n").replace('\r', '\n');
			
			if (returnValue.contains(stringQuoteString)) {
				throw new AgnTagError(AgnTagErrorKey.invalidUnquotedValue);
			}
		}

		return returnValue;
	}
}
