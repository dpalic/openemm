##################################################################################
#  The contents of this file are subject to the OpenEMM Public License Version 1.1
#  ("License"); You may not use this file except in compliance with the License.
#  You may obtain a copy of the License at http://www.agnitas.org/openemm.
#  Software distributed under the License is distributed on an "AS IS" basis,
#  WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
#  the specific language governing rights and limitations under the License.
# 
#  The Original Code is OpenEMM.
#  The Initial Developer of the Original Code is AGNITAS AG. Portions created by
#  AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
# 
#  All copies of the Covered Code must include on each user interface screen,
#  visible to all users at all times
#     (a) the OpenEMM logo in the upper left corner and
#     (b) the OpenEMM copyright notice at the very bottom center
#  See full license, exhibit B for requirements.
##################################################################################
 
CC	= gcc
CFLAGS	= -I. -I../lib $(shell xml2-config --cflags)
LDFLAGS	=
LIBS	= grammer/libgrammer.a -lslang -L../lib -lagn $(shell xml2-config --libs)
SRCS	= append.c \
	  block.c \
	  blockmail.c \
	  blockspec.c \
	  codec.c \
	  convert.c \
	  count.c \
	  counter.c \
	  create.c \
	  data.c \
	  dcache.c \
	  dyn.c \
	  eval.c \
	  field.c \
	  fix.c \
	  generate.c \
	  links.c \
	  mailtype.c \
	  media.c \
	  misc.c \
	  modify.c \
	  none.c \
	  parse.c \
	  postfix.c \
	  receiver.c \
	  replace.c \
	  sqllike.c \
	  tag.c \
	  tagpos.c \
	  url.c \
	  xmlback.c \
	  dtd.c
OBJS	= $(SRCS:%.c=%.o)

all::	version.h
	@$(MAKE) -C ../lib $@

all clean::
	make -C grammer $@

all::	xmlback

xmlback:	version.h $(OBJS) grammer/libgrammer.a
	$(CC) $(LDFLAGS) $(OBJS) -o $@ $(LIBS)

grammer/libgrammer.a:
	make -C grammer

clean::
	rm -f xmlback *.o *~ dtd.c version.h

version.h:	blockmail.dtd
	( echo "# ifndef	__VERSION_H" ;			\
	  echo "# define	__VERSION_H		1" ;	\
	  echo "# define	XML_VERSION		\"`grep 'ATTLIST version current' | cut -d\\\" -f2 -s`\"" ; \
	  echo "# endif		/* __VERSION_H */" ;    \
	) < $< > $@
		      
dtd.c:		blockmail.dtd
	( echo "# include	\"xmlback.h\"" ;		\
	  echo "" ;						\
	  echo "char	dtd[] = " ;				\
	  sed -e 's/"/\\"/g' -e 's/\(.*\)/        "\1\\n"/g' ;	\
	  echo "	\"\\n\";" ) < $< >$@
