/*	-*- mode: c; mode: fold -*-	*/
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
/** @file buffer.c
 * Buffer handling.
 * This module offers routines to work with a buffer, memory
 * allocation, resizing etc. is handled here.
 */
# include	<stdlib.h>
# include	"agn.h"

# ifdef		__OPTIMIZE__
# undef		buffer_stiff
# undef		buffer_stiffb
# undef		buffer_stiffch
# undef		buffer_stiffnl
# undef		buffer_stiffcrlf
# undef		buffer_iseol
# endif		/* __OPTIMIZE__ */

/** Allocate a buffer.
 * All elements are set and a buffer is preallocated, 
 * if <i>nsize</i> is bigger than 0
 * @param nsize the initial size of the buffer
 * @return the allocated struct on success, otherwise NULL
 */
buffer_t *
buffer_alloc (int nsize) /*{{{*/
{
	buffer_t	*b;
	
	if (b = (buffer_t *) malloc (sizeof (buffer_t))) {
		b -> length = 0;
		b -> size = nsize;
		b -> buffer = NULL;
		b -> spare = 0;
		if ((b -> size > 0) && (! (b -> buffer = malloc (b -> size))))
			b = buffer_free (b);
	}
	return b;
}/*}}}*/
/** Frees a buffer.
 * The memeory used by the buffer, if in use, and the struct
 * are returned to the system
 * @param b the buffer to free
 * @return NULL
 */
buffer_t *
buffer_free (buffer_t *b) /*{{{*/
{
	if (b) {
		if (b -> buffer)
			free (b -> buffer);
		free (b);
	}
	return NULL;
}/*}}}*/
/** Set buffer size.
 * The buffer size is set to the new value. If the new value
 * is bigger than the old one, the buffer storage is increased
 * @param b the buffer
 * @param nsize the new size
 * @return true if new size could be used, false otherwise
 */
bool_t
buffer_size (buffer_t *b, int nsize) /*{{{*/
{
	if (nsize > b -> size) {
		byte_t	*temp;
		
		if (temp = realloc (b -> buffer, nsize + b -> spare)) {
			b -> buffer = temp;
			b -> size = nsize + b -> spare;
		}
	}
	return nsize <= b -> size ? true : false;
}/*}}}*/
/** Set buffer content from byte array.
 * Set new content to the buffer
 * @param b the buffer to use
 * @param data the content to use
 * @param dlen length of content
 * @return true if content could be set, false otherwise
 */
bool_t
buffer_set (buffer_t *b, const byte_t *data, int dlen) /*{{{*/
{
	bool_t	st;
	
	st = false;
	if ((dlen <= b -> size) || buffer_size (b, dlen)) {
		if (dlen > 0)
			memcpy (b -> buffer, data, dlen);
		b -> length = dlen;
		st = true;
	}
	return st;
}/*}}}*/
/** Set buffer content from byte.
 * @param b the buffer to use
 * @param data the content
 * @return true on success, false otherwise
 * @see buffer_set
 */
bool_t
buffer_setb (buffer_t *b, byte_t data) /*{{{*/
{
	return buffer_set (b, & data, 1);
}/*}}}*/
/** Set buffer content from string with length.
 * @param b the buffer to use
 * @param str the content
 * @param len the length of content
 * @return true on success, false otherwise
 * @see buffer_set
 */
bool_t
buffer_setsn (buffer_t *b, const char *str, int len) /*{{{*/
{
	return buffer_set (b, (const byte_t *) str, len);
}/*}}}*/
/** Set buffer content from string.
 * @param b the buffer to use
 * @param str the content
 * @return true on success, false otherwise
 * @see buffer_set
 */
bool_t
buffer_sets (buffer_t *b, const char *str) /*{{{*/
{
	return buffer_set (b, (const byte_t *) str, strlen (str));
}/*}}}*/
/** Set buffer content from character.
 * @param b the buffer to use
 * @param ch the content
 * @return true on success, false otherwise
 * @see buffer_set
 */
bool_t
buffer_setch (buffer_t *b, char ch) /*{{{*/
{
	return buffer_setb (b, (byte_t) ch);
}/*}}}*/
/** Append byte array to buffer.
 * Append content to the buffer, allocating memory, if required
 * @param b the buffer to use
 * @param data the content
 * @param dlen the length of the content
 * @return true if content could be appended, false otherwise
 */
bool_t
buffer_append (buffer_t *b, const byte_t *data, int dlen) /*{{{*/
{
	bool_t	st;
	
	st = false;
	if ((b -> length + dlen <= b -> size) || buffer_size (b, b -> length + dlen)) {
		if (dlen > 0) {
			memcpy (b -> buffer + b -> length, data, dlen);
			b -> length += dlen;
		}
		st = true;
	}
	return st;
}/*}}}*/
/** Append buffer content to buffer.
 * @param b the buffer to use
 * @param data the content
 * @return true on success, false otherwise
 * @see buffer_append
 */
bool_t
buffer_appendbuf (buffer_t *b, buffer_t *data) /*{{{*/
{
	return data ? buffer_append (b, data -> buffer, data -> length) : true;
}/*}}}*/
/** Append byte to buffer.
 * @param b the buffer to use
 * @param data the content
 * @return true on success, false otherwise
 * @see buffer_append
 */
bool_t
buffer_appendb (buffer_t *b, byte_t data) /*{{{*/
{
	return buffer_append (b, & data, 1);
}/*}}}*/
/** Append string with length to buffer.
 * @param b the buffer to use
 * @param str the string
 * @param len its length
 * @return true on success, false otherwise
 * @see buffer_append
 */
bool_t
buffer_appendsn (buffer_t *b, const char *str, int len) /*{{{*/
{
	return buffer_append (b, (byte_t *) str, len);
}/*}}}*/
/** Append string to buffer.
 * @param b the buffer to use
 * @param str the content
 * @return true on success, false otherwise
 * @see buffer_append
 */
bool_t
buffer_appends (buffer_t *b, const char *str) /*{{{*/
{
	return buffer_append (b, (byte_t *) str, strlen (str));
}/*}}}*/
/** Append character to buffer.
 * @param b the buffer to use
 * @param ch the content
 * @return true on success, false otherwise
 * @see buffer_append
 */
bool_t
buffer_appendch (buffer_t *b, char ch) /*{{{*/
{
	return buffer_appendb (b, (byte_t) ch);
}/*}}}*/
/** Append newline to buffer.
 * @param b the buffer to use
 * @return true on success, false otherwise
 * @see buffer_append
 */
bool_t
buffer_appendnl (buffer_t *b) /*{{{*/
{
	return buffer_appendb (b, (byte_t) '\n');
}/*}}}*/
/** Append CR+LF to buffer.
 * @param b the buffer to use
 * @return true on success, false otherwise
 * @see buffer_append
 */
bool_t
buffer_appendcrlf (buffer_t *b) /*{{{*/
{
	return buffer_append (b, (byte_t *) "\r\n", 2);
}/*}}}*/
/** Insert data at given position
 * @param b the buffer to use
 * @param pos the position to insert data
 * @param data the content
 * @param dlen length of content
 * @return true if content could be inserted, false otherwise
 */
bool_t
buffer_insert (buffer_t *b, int pos, const byte_t *data, int dlen) /*{{{*/
{
	bool_t	st;
	
	st = false;
	if ((b -> length + dlen <= b -> size) || buffer_size (b, b -> length + dlen)) {
		if (pos < 0)
			pos = 0;
		if (pos < b -> length)
			memmove (b -> buffer + pos + dlen, b -> buffer + pos, b -> length - pos);
		if (dlen > 0) {
			memcpy (b -> buffer + pos, data, dlen);
			b -> length += dlen;
		}
		st = true;
	}
	return st;
}/*}}}*/
bool_t
buffer_insertbuf (buffer_t *b, int pos, buffer_t *data) /*{{{*/
{
	return data ? buffer_insert (b, pos, data -> buffer, data -> length) : true;
}/*}}}*/
bool_t
buffer_insertsn (buffer_t *b, int pos, const char *str, int len) /*{{{*/
{
	return buffer_insert (b, pos, (byte_t *) str, len);
}/*}}}*/
bool_t
buffer_inserts (buffer_t *b, int pos, const char *str) /*{{{*/
{
	return buffer_insert (b, pos, (byte_t *) str, strlen (str));
}/*}}}*/
/** Stiff (append) byte array to buffer.
 * This is like <i>buffer_append</i>, but more buffer is allocated
 * than currently required. These class of functions are useful when
 * there are many small appends to avoid a memory reallocation on
 * each call
 * @param b the buffer to use
 * @param data the content
 * @param dlen the size of the content
 * @return true if content could be appended, false otherwise
 */
bool_t
buffer_stiff (buffer_t *b, const byte_t *data, int dlen) /*{{{*/
{
	bool_t	st;
	
	st = false;
	if ((b -> length + dlen < b -> size) || buffer_size (b, b -> length + dlen + b -> size)) {
		if (dlen > 0) {
			memcpy (b -> buffer + b -> length, data, dlen);
			b -> length += dlen;
		}
		st = true;
	}
	return st;
}/*}}}*/
/** Stiff (append) byte to buffer.
 * @param b the buffer to use
 * @param data content
 * @return true on success, false otherwise
 * @see buffer_stiff
 */
bool_t
buffer_stiffb (buffer_t *b, byte_t data) /*{{{*/
{
	return buffer_stiff (b, & data, 1);
}/*}}}*/
/** Stiff (append) string with length to buffer.
 * @param b the buffer to use
 * @param str content
 * @param len length of string
 * @return true on success, false otherwise
 * @see buffer_stiff
 */
bool_t
buffer_stiffsn (buffer_t *b, const char *str, int len) /*{{{*/
{
	return buffer_stiff (b, (byte_t *) str, len);
}/*}}}*/
/** Stiff (append) string to buffer.
 * @param b the buffer to use
 * @param str content
 * @return true on success, false otherwise
 * @see buffer_stiff
 */
bool_t
buffer_stiffs (buffer_t *b, const char *str) /*{{{*/
{
	return buffer_stiff (b, (byte_t *) str, strlen (str));
}/*}}}*/
/** Stiff (append) character to buffer.
 * @param b the buffer to use
 * @param ch content
 * @return true on success, false otherwise
 * @see buffer_stiff
 */
bool_t
buffer_stiffch (buffer_t *b, char ch) /*{{{*/
{
	return buffer_stiffb (b, (byte_t) ch);
}/*}}}*/
/** Stiff (append) newline to buffer.
 * @param b the buffer to use
 * @return true on success, false otherwise
 * @see buffer_stiff
 */
bool_t
buffer_stiffnl (buffer_t *b) /*{{{*/
{
	return buffer_stiffb (b, (byte_t) '\n');
}/*}}}*/
/** Stiff (append) CR+LF to buffer.
 * @param b the buffer to use
 * @return true on success, false otherwise
 * @see buffer_stiff
 */
bool_t
buffer_stiffcrlf (buffer_t *b) /*{{{*/
{
	return buffer_stiff (b, (byte_t *) "\r\n", 2);
}/*}}}*/
/** Append vprinf like format to buffer.
 * Build new string using the format and given paramter using
 * the printf rules
 * @param b the buffer to use
 * @param fmt the prinf like format
 * @param par the parameter for format
 * @return true if formated string could be appended, false otherwise
 */
bool_t
buffer_vformat (buffer_t *b, const char *fmt, va_list par) /*{{{*/
{
	bool_t	st;
	int	len, room;
	int	n;
	
	n = 128;
	do {
		st = false;
		len = n + 1;
		room = b -> size - b -> length;
		if ((room < len) && (! buffer_size (b, b -> length + len + 1)))
			break;
		st = true;
		n = vsnprintf (b -> buffer + b -> length, len, fmt, par);
		if (n == -1)
			n = len * 2;
	}	while (n >= len);
	if (st)
		b -> length += n;
	return st;
}/*}}}*/
/** Append printf like format to buffer.
 * Like <i>buffer_vformat</i>, but takes variable number of arguments
 * @param b the buffer to use
 * @param fmt the printf like format
 * @param ... the paramter list
 * @return true on success, false otherwise
 * @see buffer_vformat
 */
bool_t
buffer_format (buffer_t *b, const char *fmt, ...) /*{{{*/
{
	va_list	par;
	bool_t	st;
	
	va_start (par, fmt);
	st = buffer_vformat (b, fmt, par);
	va_end (par);
	return st;
}/*}}}*/
/** Append time format to buffer.
 * Use the formating capabilities of <b>strftime(3)</b> to append
 * date/time informations to the buffer
 * @param b the buffer to use
 * @param fmt the strftime format
 * @param tt the time to use
 * @return true on success, false otherwise
 */
bool_t
buffer_strftime (buffer_t *b, const char *fmt, const struct tm *tt) /*{{{*/
{
	bool_t	st;
	
	if (st = buffer_size (b, b -> length + strlen (fmt) * 4 + 256 + 1)) 
		b -> length += strftime (b -> buffer + b -> length, b -> size - b -> length - 1, fmt, tt);
	return st;
}/*}}}*/
/** Cuts a piece of the buffer.
 * A part of the buffer is cut out, a new memory block is allocated
 * for the cut out copy to be returned (which must be freed using
 * <b>free(3)</b>).
 * @param b the buffer to use
 * @param start the start position to cut out
 * @param length the length to cut out
 * @param rlength the length of the returned buffer
 * @return the cut out piece in newly allocated memory on success, NULL otherwise
 */
byte_t *
buffer_cut (buffer_t *b, long start, long length, long *rlength) /*{{{*/
{
	byte_t	*ret;
	int	rlen;
	
	if (start >= b -> length)
		rlen = 0;
	else if (start + length >= b -> length)
		rlen = b -> length - start;
	else
		rlen = length;
	if (ret = (byte_t *) malloc ((rlen + 1) * sizeof (byte_t))) {
		if (rlen > 0)
			memcpy (ret, b -> buffer + start, rlen * sizeof (byte_t));
		ret[rlen] = 0;
	}
	if (rlength)
		*rlength = rlen;
	return ret;
}/*}}}*/
/** Returns the buffer as string.
 * A nul byte will be appended to current content and the
 * content of the buffer will be returned. Be sure not to
 * alter or free the returned value in any way!
 * @param b the buffer to use
 * @return the pointer to the start of the real buffer on success, NULL otherwise
 */
const char *
buffer_string (buffer_t *b) /*{{{*/
{
	if (buffer_size (b, b -> length + 1)) {
		b -> buffer[b -> length] = '\0';
		return (const char *) b -> buffer;
	}
	return NULL;
}/*}}}*/

/** Checks for EOL.
 * The buffer is checked at given position, if there is an EOL condition.
 * The number of bytes marking the EOL condition is returned, if one is found
 * @param b the buffer to use
 * @param pos the position to check for EOL
 * @return 0, if no EOL condition is found, the length of the EOL condition otherwise
 */
int
buffer_iseol (const buffer_t *b, int pos) /*{{{*/
{
	if (pos < b -> length) {
		if (b -> buffer[pos] == '\n')
			return 1;
		if ((b -> buffer[pos] == '\r') && (pos + 1 < b -> length) && (b -> buffer[pos + 1] == '\n'))
			return 2;
	}
	return 0;
}/*}}}*/
			
