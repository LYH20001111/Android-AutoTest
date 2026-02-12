@echo off
echo y|xcopy  OUTPUT\compatibleEMV\pom.xml mesdk\pom.xml
del  mesdk\src\com\newland\sdk\ModuleManage.java
echo f|xcopy  OUTPUT\compatibleEMV\ModuleManage.java  mesdk\src\com\newland\sdk\ModuleManage.java
echo y|xcopy  OUTPUT\compatibleEMV\ModuleManage.java  buildTask\src\com\newland\sdk\ModuleManage.java
call mvn clean install javadoc:javadoc -Dmaven.test.skip=true -Pprod
del  mesdk\src\com\newland\sdk\ModuleManage.java

echo --SDK(compatible)--PACK--
cd OUTPUT
cd compatibleEMV
rmdir /s /q mesdkPack
mkdir mesdkPack
mkdir mesdkPack\libs
mkdir mesdkPack\doc

xcopy  ..\..\ThirdParty\L3EMVLibs\* mesdkPack\libs /s/e/y
xcopy  ..\..\mesdk\target\*.jar  mesdkPack\libs
xcopy  ..\..\doc\Newland_Mesdk_Specification\Newland_Mesdk_Specification-v1.0.5.pdf  mesdkPack\doc
xcopy  ..\..\doc\TTF_Script_print_command_standard.pdf  mesdkPack\doc
xcopy  ..\..\target\site\*  mesdkPack\doc /s/e/y
xcopy  ..\..\doc\Newland_Mesdk_Specification\emverrcode.h  mesdkPack\doc
xcopy  ..\..\doc\Newland_Mesdk_Specification\Log_Authorization-v1.0_20210222.pdf mesdkPack\doc

ren mesdkPack\doc\apidocs NewlandSDKAPI

cd ../../
pause