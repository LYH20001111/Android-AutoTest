cp OUTPUT/compatibleEMV/pom.xml mesdk/pom.xml
rm mesdk/src/com/newland/sdk/ModuleManage.java
cp OUTPUT/compatibleEMV/ModuleManage.java mesdk/src/com/newland/sdk/ModuleManage.java
cp OUTPUT/compatibleEMV/ModuleManage.java buildTask/src/com/newland/sdk/ModuleManage.java
mvn clean install javadoc:javadoc -Dmaven.test.skip=true -Pprod
rm mesdk/src/com/newland/sdk/ModuleManage.java

echo --SDK（EMV L3）--PACK--
cd OUTPUT
cd compatibleEMV
rm -rf mesdkPack
mkdir mesdkPack
mkdir mesdkPack/libs
mkdir mesdkPack/doc
pwd
cp -R ../../ThirdParty/compatibleEMVLibs/* mesdkPack/libs
cp ../../mesdk/target/*.jar mesdkPack/libs
cp  ../../doc/Newland_Mesdk_Specification/Newland_Mesdk_Specification-v1.0.5.pdf mesdkPack/doc
cp  ../../doc/TTF_Script_print_command_standard.pdf mesdkPack/doc
cp  -R ../../target/site/* mesdkPack/doc
cp  ../../doc/Newland_Mesdk_Specification/emverrcode.h  mesdkPack/doc
cp  ../../doc/Newland_Mesdk_Specification/Log_Authorization-v1.0_20210222.pdf  mesdkPack/doc
mv mesdkPack/doc/apidocs mesdkPack/doc/NewlandSDKAPI