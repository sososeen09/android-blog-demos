
> 注意:使用命令行打包，注意需要配置好环境变量，确保adb 、dx、aapt等命令都可以正常使用

# 打包方式

本项目中使用了gradle和shell脚本两种不同的方式打包

- 使用脚本打包，进入 `apk-build-with-shell` 目录，直接执行 `apk-build-with-shell/installApk.sh` 脚本即可实现打包、安装、启动过程
- 直接运行 multidexbuild 项目

运行效果如下图：

<img src="art/screen_shot_1.png" width=260><img src="art/screen_shot_2.png" width=260><img src="art/screen_shot_3.png" width=260>

直接点击getResult按钮会crash，因为 `10/0 `，运行期肯定会报错。点击fix 按钮之后通过热修复的方式把代码做了更改，把代码中的 `10/0` 改成了 `10/1`，然后再点击 getResult 按钮的时候就没问题了。




1 SimpleMathUtils类中10/0，运行时会报错

```
package com.sososeen09.multidexbuild;

public class SimpleMathUtils {
	public static String divide(){
		int a=10/0;
		return "the divide result is  "+a;
	}
}
```

2 动态加载一个dex达到热修复目的

需要修复一下SimpleMathUtils的这个方法

```
public static String divide(){
		int a=10/1;
		return "the divide result is  "+a;
}
```
 
通过命令行把修复后的类重新编译，并生成dex文件

```
javac -encoding UTF-8 -g -target 1.7 -source 1.7 -d bin src/main/java/com/sososeen09/multidexbuild/SimpleMathUtils.java
cd bin
dx --dex --output=fixed.dex com/sososeen09/multidexbuild/SimpleMathUtils.class
```

3 本例子为了方便，已经把修复好的dex放在了assets中，运行时通过点击fix就可以记载assets中的dex文件

# blog

- [Apk文件构建流程](https://www.jianshu.com/p/c8ccf7ffa79e)