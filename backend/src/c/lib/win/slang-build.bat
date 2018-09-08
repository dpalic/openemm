@echo off
rem	-*- no -*-
rem
rem	Simple build skript to build slang-1.4.9 if
rem	supplied build tools do not work
rem
cl /DWIN32 /I. "/IC:\Programme\Microsoft Visual Studio 8\VC\include" /c slang.c slarray.c slclass.c slcmd.c slerr.c slgetkey.c slkeymap.c slmalloc.c slmath.c slarith.c slassoc.c slmemchr.c slmemcmp.c slmemcpy.c slmemset.c slmisc.c slparse.c slprepr.c slregexp.c slrline.c slsearch.c slsmg.c slstd.c sltoken.c sltypes.c slscroll.c slsignal.c slkeypad.c slerrno.c slstring.c slstruct.c slcmplex.c slarrfun.c slimport.c slpath.c slcompat.c slstdio.c slproc.c sltime.c slstrops.c slbstr.c slpack.c slintall.c slistruc.c slposio.c slnspace.c slarrmis.c slospath.c slscanf.c slxstrng.c
link -lib -out:libslang.lib *.obj
