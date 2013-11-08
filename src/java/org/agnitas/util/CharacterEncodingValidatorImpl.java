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
 * the code written by AGNITAS AG are Copyright (c) 2007 AGNITAS AG. All Rights
 * Reserved.
 * 
 * Contributor(s): AGNITAS AG. 
 ********************************************************************************/

package org.agnitas.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.agnitas.beans.DynamicTag;
import org.agnitas.beans.DynamicTagContent;
import org.agnitas.beans.Mailing;
import org.agnitas.beans.MailingComponent;
import org.agnitas.beans.MediatypeEmail;
import org.agnitas.exceptions.CharacterEncodingValidationException;
import org.agnitas.web.MailingContentForm;
import org.agnitas.web.forms.MailingBaseForm;

/**
 * This class validates the content of a mailing against the character set
 * defined in the mailing.
 * 
 * 
 * @author Markus Dörschmidt
 *
 */
public class CharacterEncodingValidatorImpl implements CharacterEncodingValidator {

	/**
	 * Validates text and HTML template of the given form and the content blocks of the given Mailing object. This method is called directly <b>before modifying</b>
	 * the MailingBaseForm object. 
	 * 
	 * @param form form to validate text and HTML template
	 * @param mailing mailing to validate content blocks
	 * @param unencodeableMailingComponents
	 * @param unencodeableDynamicTags
	 */
	public void validate( MailingBaseForm form, Mailing mailing) throws CharacterEncodingValidationException {
		Set<String> unencodeableMailingComponents = new HashSet<String>();
		Set<String> unencodeableDynamicTags = new HashSet<String>();
		
		boolean subjectValid = validate( form, mailing, unencodeableMailingComponents, unencodeableDynamicTags);
		
		if( unencodeableMailingComponents.size() > 0 || unencodeableDynamicTags.size() > 0 || !subjectValid)
			throw new CharacterEncodingValidationException( subjectValid, unencodeableMailingComponents, unencodeableDynamicTags);
	}
	
	private boolean validate( MailingBaseForm form, Mailing mailing, Set<String> unencodeableMailingComponents, Set<String> unencodeableDynamicTags) {
		CharsetEncoder charsetEncoder = getCharsetEncoder( form);
		
		validate( form, unencodeableMailingComponents, charsetEncoder);
		validateDynamicTags( mailing, unencodeableDynamicTags, charsetEncoder);
		
		return validate( form.getEmailSubject(), charsetEncoder);
	}
	
	public void validate( MailingContentForm form, Mailing mailing) throws CharacterEncodingValidationException {
		Set<String> unencodeableDynamicTags = new HashSet<String>();
		
		validate( form, mailing, unencodeableDynamicTags);
		
		if( unencodeableDynamicTags.size() > 0)
			throw new CharacterEncodingValidationException( new HashSet<String>(), unencodeableDynamicTags);
	}
	
	private void validate( MailingContentForm form, Mailing mailing, Set<String> unencodeableDynamicTags) {
		CharsetEncoder charsetEncoder = getCharsetEncoder( mailing);
		
		validate( form, charsetEncoder, unencodeableDynamicTags);
	}
	
	private void validate( MailingContentForm form, CharsetEncoder charsetEncoder, Set<String> unencodeableDynamicTags) {
		Map<String, DynamicTagContent> dynTags = form.getContent();
		
		for( DynamicTagContent dynamicTagContent : dynTags.values()) {
			if( !validate( dynamicTagContent, charsetEncoder))
				unencodeableDynamicTags.add( form.getDynName());
		}
	}
	
	/**
	 * Validates a mailing component.
	 * 
	 * @param component the mailing component to be validated
	 * @param charsetName the name of the character set to be used
	 * @return true if the mailing component passed validated otherwise false
	 */
	public boolean validate( MailingComponent component, String charsetName) {
		CharsetEncoder charsetEncoder = getCharsetEncoder( charsetName);
		return validate( component, charsetEncoder);
	}
	
	/**
	 * Validates a dynamic tag.
	 * 
	 * @param dynTag DynamicTag to be validated
	 * @param charsetName character set to be used for validation
	 * @return true if the DynamicTag passed validated otherwise false
	 */
	public boolean validate( DynamicTag dynTag, String charsetName) {
		CharsetEncoder charsetEncoder = getCharsetEncoder( charsetName);
		return validate( dynTag, charsetEncoder);
	}
	
	private void validate( MailingBaseForm form, Set<String> unencodeableMailingComponents, CharsetEncoder encoder) {
		if( !validate( form.getTextTemplate(), encoder))
			unencodeableMailingComponents.add( "agnText");
		if( !validate( form.getHtmlTemplate(), encoder))
			unencodeableMailingComponents.add( "agnHtml");
	}
	
	private void validateDynamicTags( Mailing mailing, Set<String> unencodeableDynamicTags, CharsetEncoder charsetEncoder) {
		// No mailing? Nothing to validate!
		if( mailing == null)
			return;
		
		Collection<DynamicTag> dynTags = mailing.getDynTags().values();

		for( DynamicTag dynTag : dynTags)
			if( !validate( dynTag, charsetEncoder))
				unencodeableDynamicTags.add( dynTag.getDynName());
	}

	/**
	 * Validates a mailing component using the given CharsetEncoder.
	 * 
	 * @param component the mailing component to be validated
	 * @param charsetEncoder CharsetEncoder to be used for validation
	 * @return true if the mailing component passed validated otherwise false
	 */
	private boolean validate( MailingComponent component, CharsetEncoder charsetEncoder) {
		return validate( component.getEmmBlock(), charsetEncoder);
	}
	
	/**
	 * Validates a dynamic tag using the given CharsetEncoder.
	 * 
	 * @param dynTag DynamicTag to be validated
	 * @param charsetEncoder CharsetEncoder to be used for validation
	 * @return true if the DynamicTag passed validated otherwise false
	 */
	private boolean validate( DynamicTag dynTag, CharsetEncoder charsetEncoder) {
		Collection<DynamicTagContent> contents = dynTag.getDynContent().values();
		
		for( DynamicTagContent content : contents)
			if( !validate( content, charsetEncoder))
				return false;
		
		return true;
	}

	/**
	 * Validates the content of a DynamicTagContent objects
	 * @param content object to be validates
	 * @param charsetEncoder CharacterEncoder to be used for validation
	 * @return
	 */
	private boolean validate( DynamicTagContent content, CharsetEncoder charsetEncoder) {
		return validate( content.getDynContent(), charsetEncoder);
	}
	
	/**
	 * Validates a String using a CharsetEncoder.
	 * 
	 * @param string String to be validated
	 * @param charsetEncoder CharsetEncoder to be used for validation
	 * @return true if the String passed the validation otherwise false
	 */
	private boolean validate( String string, CharsetEncoder charsetEncoder) {
		return charsetEncoder.canEncode( string);
	}

	/**
	 * Returns a CharsetEncoder matching the character set defined in the given mailing.
	 * @param mailing Mailing to create CharsetEncoder for
	 * @return CharsetEncoder for mailing
	 */
	private CharsetEncoder getCharsetEncoder( Mailing mailing) {
		String charsetName = ((MediatypeEmail) mailing.getMediatypes().get( 0)).getCharset();

		return getCharsetEncoder( charsetName);
	}
	
	private CharsetEncoder getCharsetEncoder( MailingBaseForm form) {
		return getCharsetEncoder( form.getEmailCharset());
	}
	
	/**
	 * Creates a CharsetEncoder for the given charset.
	 * 
	 * @param charsetName name of character set
	 * @return CharsetEncoder for given charset
	 */
	private CharsetEncoder getCharsetEncoder( String charsetName) {
		Charset charset = Charset.forName( charsetName);
		
		return charset.newEncoder();
	}
}
