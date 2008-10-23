@echo off
rem	-*- no -*-
cl /DWIN32 /I. /Iwin "/IC:\Programme\Microsoft Visual Studio 8\VC\include" /c  atob.c buffer.c hash.c lock.c log.c node.c map.c skip.c str.c tzdiff.c var.c compat.c
