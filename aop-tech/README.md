# AOP开发技术

使用AOP技术在方法调用上统一处理判断执行方法的条件，例如是否已经登录、绑定手机号。
在示例中演示了未登录、未绑定手机号的情况下，弹出dialog提示用户，点击确定之后去完成相应的逻辑；
如果已经登录或者绑定手机号，直接执行方法的逻辑。

这种适用于在页面中有很多地方都需要统一处理该逻辑的方式。

# 相关
- [HujiangTechnology / gradle_plugin_android_aspectjx](https://github.com/north2016/T-MVP)
- [north2016 / T-MVP](https://github.com/HujiangTechnology/gradle_plugin_android_aspectjx)