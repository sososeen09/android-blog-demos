#!/bin/bash

needAsk=$1

echo "needAsk: ${needAsk}"

funConfirm(){
    if [! -n "$needAsk"] ;then
        echo -n "Please type[y/n/yes/no]: "
        read input
        #  先转换小写到大写，再通过cut截取第一个字符。
        ret=`echo $input | tr '[a-z]' '[A-Z]' | cut -c1`

        if [ $ret = "Y" ]; then
            echo "go on"
        elif [ $ret = "N" ]; then
            echo "stop it !"
            exit
        else
            echo "Your input is error."
            funConfirm
        fi
    else
         echo "no need to ask"
    fi
}


echo 'init...'
project_dir=$(pwd)

echo "project_dir: ${project_dir}"

# 需要更改为自己的android sdk存放的目录
sdk_folder=/works/android/android-sdk-macosx
platform_folder=${sdk_folder}/platforms/android-26
android_jar=${platform_folder}/android.jar

# 使用通配符，因为有的命名是sdklib-26.0.0-dev.jar这样的形式
sdklib_jar=${sdk_folder}/tools/lib/sdklib*.jar

src=${project_dir}/src/main
bin=${project_dir}/bin
libs=${project_dir}/libs

java_source_folder=${src}/java

if [ -d gen ];then
	rm -rf gen
fi
if [ -d bin ];then
	rm -rf bin
fi
mkdir gen
mkdir bin


#1.生成R文件
echo 'generate R.java file'
aapt package -f -m -J ./gen -S ${src}/res -M ${src}/AndroidManifest.xml -I ${android_jar}

funConfirm

#2.生成资源索引文件
echo 'generate resourses index file'
aapt package -f -M ${src}/AndroidManifest.xml -S ${src}/res -I ${android_jar} -F bin/resources.ap_

funConfirm

#3.编译java文件
echo 'compile java file'
javac -encoding UTF-8 -g -target 1.7 -source 1.7 -cp ${android_jar} -d bin ${java_source_folder}/com/sososeen09/multidexbuild/*.java ${java_source_folder}/com/sososeen09/multidexbuild/utils/*.java gen/com/sososeen09/multidexbuild/*.java
# javac -encoding UTF-8 -g -target 1.7 -source 1.7 -cp $android_jar -d bin src/ gen/

funConfirm

#4.生成dex文件,这里我们把MainActivity打包到主dex中，utils打包到secondaryDex中
# --minimal-main-dex 表示只把maindexlist.txt中指定的类打包到主dex中
#  --set-max-idx-number=2000  表示指定没个dex的最大方法数目是2001，最大65535
echo 'generate dex file'
dx --dex --output=bin/ --multi-dex --main-dex-list=maindexlist.txt --minimal-main-dex bin/

funConfirm

#5.打包apk
echo 'generate apk file'
java -cp ${sdklib_jar} com.android.sdklib.build.ApkBuilderMain bin/app-debug-unsigned.apk -v -u -z bin/resources.ap_ -f bin/classes.dex -rf src
funConfirm

#6.通过aapt工具把secondarydex copy到apk中
echo 'aapt add dex into apk'
cd bin
aapt add -f app-debug-unsigned.apk classes2.dex
cd ..

#7.把assets的内容加进去
echo 'put some file into apk assets'
aapt add -f bin/app-debug-unsigned.apk assets/ic_launcher-web.png assets/fixed.dex

#8 签名
echo 'sign apk'
java -jar auto-sign/signapk.jar auto-sign/testkey.x509.pem auto-sign/testkey.pk8 ./bin/app-debug-unsigned.apk ./bin/app-debug.apk

#9 打印方法数
dexdump -f bin/app-debug.apk | grep method_ids_size

