/*	-*- mode: c; mode: fold -*-	*/
/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/
# include	"xmlback.h"

static bool_t
final_eol (buffer_t *dest, bool_t usecrlf) /*{{{*/
{
	if ((dest -> length > 0) && (! buffer_iseol (dest, dest -> length - 1)))
		return usecrlf ? buffer_appendcrlf (dest) : buffer_appendnl (dest);
	return true;
}/*}}}*/
bool_t
append_mixed (buffer_t *dest, const char *desc, ...) /*{{{*/
{
	va_list		par;
	bool_t		rc;
	int		n;
	
	va_start (par, desc);
	rc = true;
	for (n = 0; rc && desc[n]; ++n)
		switch (desc[n]) {
		case 's':
			rc = buffer_stiffs (dest, va_arg (par, const char *));
			break;
		case 'b':
			{
				xmlBufferPtr	temp;
				
				temp = va_arg (par, xmlBufferPtr);
				rc = buffer_stiff (dest, xmlBufferContent (temp), xmlBufferLength (temp));
			}
			break;
		case 'i':
			{
				int		len;
				char		scratch[32];
				
				len = sprintf (scratch, "%d", va_arg (par, int));
				rc = buffer_stiff (dest, scratch, len);
			}
			break;
		default:
			rc = false;
			break;
		}
	return rc;
}/*}}}*/
bool_t
append_pure (buffer_t *dest, const xmlBufferPtr src) /*{{{*/
{
	return buffer_stiff (dest, xmlBufferContent (src), xmlBufferLength (src));
}/*}}}*/
bool_t
append_raw (buffer_t *dest, bool_t usecrlf, const buffer_t *src) /*{{{*/
{
	if (src -> length)
		return (buffer_stiff (dest, src -> buffer, src -> length) &&
			(usecrlf ? buffer_stiffcrlf (dest) : buffer_stiffnl (dest))) ? true : false;
	return true;
}/*}}}*/
bool_t
append_cooked (buffer_t *dest, bool_t usecrlf, const xmlBufferPtr src,
	       const char *charset, encoding_t method) /*{{{*/
{
	bool_t	st;
	
	st = false;
	switch (method) {
	case EncNone:
		st = encode_none (src, dest);
		break;
	case EncHeader:
		st = encode_header (src, dest, usecrlf, charset);
		break;
	case Enc8bit:
		st = encode_8bit (src, dest, usecrlf);
		break;
	case EncQuotedPrintable:
		st = encode_quoted_printable (src, dest, usecrlf);
		break;
	case EncBase64:
		st = encode_base64 (src, dest, usecrlf);
		break;
	}
	if (st)
		st = final_eol (dest, usecrlf);
	return st;
}/*}}}*/
