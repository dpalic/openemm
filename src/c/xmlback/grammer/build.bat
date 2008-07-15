@echo off
rem	-*- no -*-
cl "/IC:\Programme\Microsoft Visual Studio 8\VC\include" lemon.c /link "/LIBPATH:C:\Programme\Microsoft Visual Studio 8\VC\lib"
lemon -c -s parse.y
cl /DWIN32 /Dinline= /I.. /I..\..\lib /I..\..\lib\win "/IC:\Programme\Microsoft Visual Studio 8\VC\include" /c transform.c parse.c
