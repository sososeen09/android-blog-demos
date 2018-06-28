package com.sososeen09.aspeactj

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.pipeline.TransformTask
import com.google.common.collect.ImmutableSet
import com.sososeen09.aspeactj.internal.AJXProcedure
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import com.google.common.io.ByteStreams

import java.util.jar.JarEntry
import java.util.jar.JarFile

class AJXTransform extends Transform {

    AJXProcedure ajxProcedure

    AJXTransform(Project project) {
        ajxProcedure = new AJXProcedure(project)
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

        def outputProvider = transformInvocation.outputProvider
        transformInvocation.inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput dirInput ->
                dirInput.file.eachFileRecurse { File item ->
                    println "~~~~~~~~~~~~directoryInputs collect aspect file:${item.absolutePath}"
                    if (AjxUtils.isAspectClass(item)) {
                        println "~~~~~~~~~~~~directoryInputs collect aspect file:${item.absolutePath}"
                        String path = item.absolutePath
                        String subPath = path.substring(dirInput.file.absolutePath.length())
                        println "~~~~~~~~~~~~directoryInputs collect aspect file subPath: ${subPath}"

                        ajxProcedure.ajxTask.aspectPath.add(item)
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

                //直接把引入的带有aspect的jar包拷贝到目标目录
                def dest = outputProvider.getContentLocation(jarInput.name
                        , jarInput.contentTypes
                        , jarInput.scopes
                        , Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
                //包含引入的library中的jar
                ajxProcedure.ajxTask.aspectPath.add(jarInput.file)
                println "~~~~~~~~~~~jarInputs collect dest file:${dest}"
                jarFile.close()
            }
        }
        ajxProcedure.ajxTask.call()
    }
}