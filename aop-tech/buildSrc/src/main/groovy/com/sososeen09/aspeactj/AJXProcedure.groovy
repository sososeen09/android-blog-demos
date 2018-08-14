package com.sososeen09.aspeactj

import com.sososeen09.aspeactj.cache.AJXCache
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.aspectj.weaver.Dump

class AJXProcedure {
    Project project
    AJXCache ajxCache

    AJXProcedure(Project project) {
        this.project = project
        //创建配置类，就是为了获取是AppPlugin 还是LibraryPlugin，提供方法返回它们的基类BaseVariant，返回bootClasspath
        def config = new AJXConfig(project)

        //缓存，就是为了把class文件输出到build/intermediates/ajx 目录下，包括依赖的aar中的class文件
        //缓存中的encoding、bootClassPath、sourceCompatibility、targetCompatibility 等都是为aspectj准备的
        ajxCache = new AJXCache(project)

        System.setProperty("aspectj.multithreaded", "true")

        project.afterEvaluate {
            config.variants.all { variant ->
                JavaCompile javaCompile = (variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile) as JavaCompile

                //设置全局参数
                ajxCache.sourceCompatibility = javaCompile.sourceCompatibility
                ajxCache.targetCompatibility = javaCompile.targetCompatibility
                ajxCache.encoding = javaCompile.options.encoding
                ajxCache.bootClassPath = config.bootClasspath.join(File.pathSeparator)
            }
        }

//        set aspectj build log output dir
        File logDir = new File(project.buildDir.absolutePath + File.separator + "outputs" + File.separator + "logs")
        if (!logDir.exists()) {
            logDir.mkdirs()
        }

        Dump.setDumpDirectory(logDir.absolutePath)
    }


}