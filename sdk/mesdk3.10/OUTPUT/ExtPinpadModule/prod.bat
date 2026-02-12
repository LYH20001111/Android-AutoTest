@echo off

cd ../../
call install.bat
cd OUTPUT\ExtPinpadModule


del rootpom.bak
del mesdkpom.bak
del NLDevice.bak
del K21Device.bak
del ModuleManage.bak

echo f|xcopy  ..\..\pom.xml rootpom.bak
echo f|xcopy  ..\..\mesdk\pom.xml mesdkpom.bak
echo f|xcopy  ..\..\mesdk\src\com\newland\sdk\me\NLDevice.java NLDevice.bak
echo f|xcopy  ..\..\mesdk\src\com\newland\sdk\me\K21Device.java K21Device.bak
echo f|xcopy  ..\..\buildTask\src\com\newland\sdk\ModuleManage.java ModuleManage.bak

echo y|xcopy  rootpom.xml  ..\..\pom.xml 
echo y|xcopy  mesdkpom.xml  ..\..\mesdk\pom.xml
echo f|xcopy  ModuleManage.java  ..\..\mesdk\src\com\newland\sdk\ModuleManage.java
echo y|xcopy  ModuleManage.java  ..\..\buildTask\src\com\newland\sdk\ModuleManage.java
echo y|xcopy  NLDevice.java  ..\..\mesdk\src\com\newland\sdk\me\NLDevice.java
echo y|xcopy  K21Device.java  ..\..\mesdk\src\com\newland\sdk\me\K21Device.java

cd ../../
call mvn clean install javadoc:javadoc -Dmaven.test.skip=true -Pprod

cd OUTPUT\ExtPinpadModule

echo y|xcopy  rootpom.bak  ..\..\pom.xml 
echo y|xcopy  mesdkpom.bak  ..\..\mesdk\pom.xml 
del ..\..\mesdk\src\com\newland\sdk\ModuleManage.java 
echo y|xcopy  ModuleManage.bak  ..\..\buildTask\src\com\newland\sdk\ModuleManage.java
echo y|xcopy  NLDevice.bak  ..\..\mesdk\src\com\newland\sdk\me\NLDevice.java
echo y|xcopy  K21Device.bak  ..\..\mesdk\src\com\newland\sdk\me\K21Device.java

del rootpom.bak
del mesdkpom.bak
del NLDevice.bak
del K21Device.bak
del ModuleManage.bak

pause