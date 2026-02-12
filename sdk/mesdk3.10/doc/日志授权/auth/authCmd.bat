@echo off
echo ********************************************** 

echo. 

echo create auth file.

echo. 

echo ********************************************** 
echo Please input SN number and Date(SNyyyyMMdd).
set /p data=
set NLS_LANG=.AL32UTF8
echo ********************************************** 
echo Begin to create auth file.
echo ********************************************** 
set /p=%data%<nul>forAuthFile.properties
openssl sha1 -sign mesdk_private_key.pem -out authFile.properties forAuthFile.properties
echo ********************************************** 
echo Create auth file success.
echo ********************************************** 
pause