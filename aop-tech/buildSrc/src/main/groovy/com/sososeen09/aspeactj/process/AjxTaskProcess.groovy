package com.sososeen09.aspeactj.process

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.sososeen09.aspeactj.AjxTask
import com.sososeen09.aspeactj.TaskManager
import com.sososeen09.aspeactj.cache.VariantCache
import org.apache.commons.io.FileUtils
import org.gradle.api.Project


class AjxTaskProcess {
    Project project
    VariantCache variantCache
    TransformInvocation transformInvocation

    TaskManager ajxTaskManager

    AjxTaskProcess(Project project, VariantCache variantCache, TransformInvocation transformInvocation) {
        this.project = project
        this.variantCache = variantCache
        this.transformInvocation = transformInvocation
        ajxTaskManager = new TaskManager(
                encoding: variantCache.ajxCache.encoding,
                ajcArgs: variantCache.ajxCache.ajcArgs,
                bootClassPath: variantCache.ajxCache.bootClassPath,
                sourceCompatibility: variantCache.ajxCache.sourceCompatibility,
                targetCompatibility: variantCache.ajxCache.targetCompatibility)
    }


    void proceed() {
        println "~~~~~~~~~~~~~~~~~~~~do aspectj real work"

        ajxTaskManager.aspectPath << variantCache.aspectDir
        ajxTaskManager.classPath << variantCache.includeFileDir

        AjxTask ajxTask = new AjxTask(project)
        //classpath error: unable to find org.aspectj.lang.JoinPoint (check that aspectjrt.jar is in your classpath)
        ajxTask.classPath = ajxTaskManager.classPath
        ajxTask.aspectPath = ajxTaskManager.aspectPath
        File outputDir = transformInvocation.outputProvider.getContentLocation("include", variantCache.includeFileContentTypes, variantCache.includeFileScopes, Format.DIRECTORY)
        ajxTask.outputDir = outputDir

        if (!outputDir.parentFile.exists()) {
            FileUtils.forceMkdir(outputDir.getParentFile())
        }

        // inpath表示要处理的class，这个应该是每个Task独有的，因为不同的Task处理的class不同
        ajxTask.inPath << variantCache.includeFileDir

        ajxTask.sourceCompatibility = ajxTaskManager.sourceCompatibility
        ajxTask.targetCompatibility = ajxTaskManager.targetCompatibility
        ajxTask.encoding = ajxTaskManager.encoding
        ajxTask.bootClassPath = ajxTaskManager.bootClassPath

        println "~~~~~~~~~~~ajxTask outputDir:${ajxTask.outputDir}"

        //process jar files
        transformInvocation.inputs.each { TransformInput input ->
            input.jarInputs.each { JarInput jarInput ->
                ajxTaskManager.classPath << jarInput.file
                // getContentLocation方法相当于创建一个对应名称表示的目录
                // 是从0 、1、2开始递增。如果是目录，名称就是对应的数字，如果是jar包就类似0.jar
                File outputJar = transformInvocation.outputProvider.getContentLocation(jarInput.name
                        , jarInput.contentTypes
                        , jarInput.scopes
                        , Format.JAR)
                if (!outputJar.getParentFile()?.exists()) {
                    outputJar.getParentFile()?.mkdirs()
                }
                //复制jar包到build/intermediates/transform/ajx目录下
                FileUtils.copyFile(jarInput.file, outputJar)

                //包含引入的library中的jar
                println "~~~~~~~~~~~jarInputs collect dest file:${outputJar}"

                //对每一个jar包都创建一个任务用于处理jar包中的class文件
                AjxTask ajxTask1 = new AjxTask(project)
                ajxTask1.sourceCompatibility = ajxTaskManager.sourceCompatibility
                ajxTask1.targetCompatibility = ajxTaskManager.targetCompatibility
                ajxTask1.encoding = ajxTaskManager.encoding
                ajxTask1.bootClassPath = ajxTaskManager.bootClassPath

                ajxTask1.outputJar = outputJar.absolutePath
                ajxTask1.inPath << jarInput.file
                ajxTask1.aspectPath = ajxTaskManager.aspectPath
                ajxTask1.classPath = ajxTaskManager.classPath
                ajxTask1.call()

            }
        }

        ajxTask.call()
    }

}
