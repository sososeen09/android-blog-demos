> 注意：用命令行打包，否则的话Apk中的assets会为空
1. SimpleMathUtils类中10/0，运行时会报错
2. 动态加载一个dex达到热修复目的
    1. 需要修复一下SimpleMathUtils的这个方法
    2. 通过命令行把修复后的类重新编译，并生成dex文件
```
javac -encoding UTF-8 -g -target 1.7 -source 1.7 -d bin src/main/java/com/sososeen09/multidexbuild/SimpleMathUtils.java
cd bin
dx --dex --output=fixed.dex com/sososeen09/multidexbuild/SimpleMathUtils.class
```
3. 本例子为了方便，已经把修复好的dex放在了assets中