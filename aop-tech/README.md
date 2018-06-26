# AOP开发技术

使用AOP技术在方法调用上统一处理判断执行方法的条件，例如是否已经登录、绑定手机号。
在示例中演示了未登录、未绑定手机号的情况下，弹出dialog提示用户，点击确定之后去完成相应的逻辑；
如果已经登录或者绑定手机号，直接执行方法的逻辑。

这种适用于在页面中有很多地方都需要统一处理该逻辑的方式。

# 说明

## 使用沪江开源插件
使用沪江开源的一个aspectj 插件库 比较简单，直接在项目根目录下的build.gradle中引入插件：

```
buildscript {
    dependencies {
        classpath 'com.android.tools.build:gradle:3.0.1'
        classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.0'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

```

在所要运行的module目录下的build.gradle中apply 插件：

```
apply plugin: 'android-aspectjx'
```

记得在library中依赖aspectjrt，这个在运行时需要

```
dependencies {
    ...
    compile 'org.aspectj:aspectjrt:1.8.10'
}
```

## 自己接入aspectj

在项目根目录build.gradle下引入aspectjtools插件：

```
buildscript {
    dependencies {
        ..
        classpath 'org.aspectj:aspectjtools:1.8.10'
        classpath 'org.aspectj:aspectjweaver:1.8.8'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```

在运行app的module目录下的build.gradle中引入：

```
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main

final def log = project.logger
final def variants = project.android.applicationVariants

variants.all { variant ->
    if (!variant.buildType.isDebuggable()) {
        log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
        return;
    }

    JavaCompile javaCompile = variant.javaCompile
    javaCompile.doLast {
        String[] args = ["-showWeaveInfo",
                         "-1.8",
                         "-inpath", javaCompile.destinationDir.toString(),
                         "-aspectpath", javaCompile.classpath.asPath,
                         "-d", javaCompile.destinationDir.toString(),
                         "-classpath", javaCompile.classpath.asPath,
                         "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
        log.debug "ajc args: " + Arrays.toString(args)

        MessageHandler handler = new MessageHandler(true);
        new Main().run(args, handler);
        for (IMessage message : handler.getMessages(null, true)) {
            switch (message.getKind()) {
                case IMessage.ABORT:
                case IMessage.ERROR:
                case IMessage.FAIL:
                    log.error message.message, message.thrown
                    break;
                case IMessage.WARNING:
                    log.warn message.message, message.thrown
                    break;
                case IMessage.INFO:
                    log.info message.message, message.thrown
                    break;
                case IMessage.DEBUG:
                    log.debug message.message, message.thrown
                    break;
            }
        }
    }
}
```

注意：这个时候，依赖aspectj的library下的build.gradle也需要做相应的修改，否则的话可能会报 `NoSuchMethodError: Aspect.aspectOf` 异常。

```


dependencies {
   ...
   // 以api的方式提供引入library的一方，这样外界就不用再去引入aspectjrt了
   // 如果是library私有的，则外界需要引入aspectjrt，否则在编译的时候不会对织入点做处理
   api 'org.aspectj:aspectjrt:1.8.10'
}



import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main

android.libraryVariants.all { variant ->
    JavaCompile javaCompile = variant.javaCompile
    javaCompile.doLast {
        String[] args = [
                "-showWeaveInfo",
                "-1.8",
                "-inpath", javaCompile.destinationDir.toString(),
                "-aspectpath", javaCompile.classpath.asPath,
                "-d", javaCompile.destinationDir.toString(),
                "-classpath", javaCompile.classpath.asPath,
                "-bootclasspath", android.bootClasspath.join(File.pathSeparator)
        ]

        MessageHandler handler = new MessageHandler(true);
        new Main().run(args, handler)

        def log = project.logger
        for (IMessage message : handler.getMessages(null, true)) {
            switch (message.getKind()) {
                case IMessage.ABORT:
                case IMessage.ERROR:
                case IMessage.FAIL:
                    log.error message.message, message.thrown
                    break;
                case IMessage.WARNING:
                case IMessage.INFO:
                    log.info message.message, message.thrown
                    break;
                case IMessage.DEBUG:
                    log.debug message.message, message.thrown
                    break;
            }
        }
    }
}
```

# 相关
- [HujiangTechnology / gradle_plugin_android_aspectjx](https://github.com/north2016/T-MVP)
- [north2016 / T-MVP](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx)
- [JakeWharton/hugo](https://github.com/JakeWharton/hugo)
- [深入理解Android之AOP](https://blog.csdn.net/innost/article/details/49387395)
- [安卓AOP三剑客:APT,AspectJ,Javassist](https://www.jianshu.com/p/dca3e2c8608a)
- [AspectJ在Android中的应用](http://www.goluck.top/2017/06/11/AspectJ%E5%9C%A8Android%E4%B8%AD%E7%9A%84%E5%BA%94%E7%94%A8/)