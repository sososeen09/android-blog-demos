package com.sososeen09.aspeactj.internal

import com.sososeen09.aspeactj.AjxConfig
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class AJXProcedure {
    Project project
    AjxTask ajxTask
    AJXProcedure(Project project) {
        this.project = project
        //创建配置类，就是为了获取是AppPlugin 还是LibraryPlugin，提供方法返回它们的基类BaseVariant，返回bootClasspath
        def config = new AjxConfig(project)
        ajxTask = new AjxTask(project)

        System.setProperty("aspectj.multithreaded", "true")

        project.afterEvaluate {
            config.variants.all { variant ->
                JavaCompile javaCompile = (variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile) as JavaCompile

                //设置参数
                ajxTask.sourceCompatibility = javaCompile.sourceCompatibility
                ajxTask.targetCompatibility = javaCompile.targetCompatibility
                ajxTask.encoding = javaCompile.options.encoding
                ajxTask.bootClasspath = config.bootClasspath.join(File.pathSeparator)

                //目前这些都不包含外部的library中的class文件
                if ((javaCompile.destinationDir.listFiles() != null)) {
                    ajxTask.inPath.addAll(javaCompile.destinationDir.listFiles().toList())
                }
                ajxTask.aspectPath.addAll(javaCompile.classpath.getFiles())
                ajxTask.classPath.addAll(javaCompile.classpath.getFiles())
            }
        }

        //set aspectj build log output dir
//        File logDir = new File(project.buildDir.absolutePath + File.separator + "outputs" + File.separator + "logs")
//        if (!logDir.exists()) {
//            logDir.mkdirs()
//        }
//
//        Dump.setDumpDirectory(logDir.absolutePath)
    }


}