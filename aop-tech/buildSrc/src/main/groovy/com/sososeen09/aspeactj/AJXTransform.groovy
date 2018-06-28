package com.sososeen09.aspeactj

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.pipeline.TransformTask
import com.google.common.collect.ImmutableSet
import com.google.common.io.ByteStreams
import com.sososeen09.aspeactj.cache.VariantCache
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile

class AJXTransform extends Transform {

    AJXProcedure ajxProcedure
    Project project

    AJXTransform(Project project) {
        ajxProcedure = new AJXProcedure(project)
        this.project = project
    }

    @Override
    String getName() {
        return "ajx"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.<QualifiedContent.ContentType> of(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        //是否支持增量编译
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        TransformTask transformTask = (TransformTask) transformInvocation.context
        AjxTask ajxTask = new AjxTask(project)
        //VariantCache 就是保存一些跟当前variant相关的一些缓存
        VariantCache variantCache = new VariantCache(ajxProcedure.project, ajxProcedure.ajxCache, transformTask.variantName)

        def outputProvider = transformInvocation.outputProvider
        transformInvocation.inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput dirInput ->
                dirInput.file.eachFileRecurse { File item ->

                    if (AjxUtils.isClassFile(item.name)) {
                        //如果是class文件，复制到build/intermediates/ajx/buildVariant/目录下
                        String path = item.absolutePath
                        String subPath = path.substring(dirInput.file.absolutePath.length())
                        variantCache.add(item, new File(variantCache.cachePath, subPath))

                        if (AjxUtils.isAspectClass(item)) {
                            println "~~~~~~~~~~~~directoryInputs collect aspect file:${item.absolutePath}"
                            println "~~~~~~~~~~~~directoryInputs collect aspect file subPath: ${subPath}"
                            ajxTask.aspectPath.add(item)
                        }
                    }
                }
            }

            input.jarInputs.each { JarInput jarInput ->
                JarFile jarFile = new JarFile(jarInput.file)
                Enumeration<JarEntry> entries = jarFile.entries()
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement()
                    String entryName = jarEntry.getName()
                    if (!jarEntry.isDirectory() && AjxUtils.isClassFile(entryName)) {
                        byte[] bytes = ByteStreams.toByteArray(jarFile.getInputStream(jarEntry))
                        if (AjxUtils.isAspectClass(bytes)) {
                            println "~~~~~~~~~~~jarInputs collect aspect file:${entryName}"
                        }
                    }
                }

                File outputJar = outputProvider.getContentLocation(jarInput.name
                        , jarInput.contentTypes
                        , jarInput.scopes
                        , Format.JAR)
                if (!outputJar.getParentFile()?.exists()) {
                    outputJar.getParentFile()?.mkdirs()
                }

                //对没一个jar包都创建一个任务用于处理jar包中的class文件
                AjxTask ajxTask1 = new AjxTask(ajxProcedure.project)

                //包含引入的library中的jar
                ajxTask1.aspectPath.add(jarInput.file)
                println "~~~~~~~~~~~jarInputs collect dest file:${outputJar}"
                jarFile.close()
            }
        }
        //目前这些都不包含外部的library中的class文件
//        if (javaCompile.destinationDir.listFiles() != null) {
//            ajxTask.inPath.addAll(javaCompile.destinationDir.listFiles().toList())
//        }
//        ajxTask.aspectPath.addAll(javaCompile.classpath.getFiles())
//        ajxTask.classPath.addAll(javaCompile.classpath.getFiles())
//
//        ajxTask.outputDir = globalAjxCache.cachePath
//        ajxTask.call()
    }
}