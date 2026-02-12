@echo off
call mvn install:install-file -Dfile=ThirdParty/newland.jar -DgroupId=com.newland -DartifactId=newland -Dversion=53.1 -Dpackaging=jar
call mvn install:install-file -Dfile=ThirdParty/EmvJNIService.jar -DgroupId=com.newland.emv -DartifactId=EmvJNIService  -Dversion=4.0.15 -Dpackaging=jar
call mvn install:install-file -Dfile=ThirdParty/intelligentLibrary.jar -DgroupId=com.newland.intelLib -DartifactId=intelligentLibrary  -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile=ThirdParty/ndk.jar -DgroupId=com.newland.ndk -DartifactId=ndk  -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile=ThirdParty/k21Transation.jar -DgroupId=com.newland -DartifactId=k21Transation  -Dversion=1.0 -Dpackaging=jar
call mvn install:install-file -Dfile=ThirdParty/support-annotations-28.0.0-rc02.jar -DgroupId=com.android.support -DartifactId=support-annotations  -Dversion=28.0.0 -Dpackaging=jar
call mvn install:install-file -Dfile=ThirdParty/appcompat-v7-28.0.0.jar -DgroupId=com.android.support -DartifactId=support-appcompat  -Dversion=28.0.0 -Dpackaging=jar
call mvn install:install-file -Dfile=ThirdParty/BluetoothControl_1.0.27.3.jar -DgroupId=com.newland.bletoothbase -DartifactId=bletoothbase  -Dversion=1.0.27.3 -Dpackaging=jar
call mvn install:install-file -Dfile=ThirdParty/MESDK-RKL-1.1.0.jar -DgroupId=com.newland.rkl -DartifactId=rkl  -Dversion=1.1.0 -Dpackaging=jar

call mvn install:install-file -Dfile=android-4.2.jar -DgroupId=com.google.android -DartifactId=android  -Dversion=4.2 -Dpackaging=jar
call mvn install:install-file -Dfile=android-5.0.jar -DgroupId=com.google.android -DartifactId=android  -Dversion=5.0 -Dpackaging=jar
call mvn install:install-file -Dfile=android-7.1.1.jar -DgroupId=com.google.android -DartifactId=android  -Dversion=7.1.1 -Dpackaging=jar
call mvn install:install-file -Dfile=ThirdParty/k21.jar -DgroupId=com.newland -DartifactId=k21-driver  -Dversion=1.0.3 -Dpackaging=jar
call mvn install:install-file -Dfile=ThirdParty/UartPort.jar -DgroupId=com.newland.uartport -DartifactId=UartPort  -Dversion=1.0.2 -Dpackaging=jar
call mvn install:install-file -Dfile=ThirdParty/stream-1.2.1.jar -DgroupId=com.annimon -DartifactId=stream  -Dversion=1.2.1 -Dpackaging=jar