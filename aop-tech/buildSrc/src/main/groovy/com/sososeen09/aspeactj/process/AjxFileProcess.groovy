package com.sososeen09.aspeactj.process

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.google.common.io.ByteStreams
import com.sososeen09.aspeactj.AJXUtils
import com.sososeen09.aspeactj.cache.VariantCache
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * 用于解析class文件和相关的jar包
 */
class AjxFileProcess {
    Project project
    VariantCache variantCache
    TransformInvocation transformInvocation

    AjxFileProcess(Project project, VariantCache variantCache, TransformInvocation transformInvocation) {
        this.project = project
        this.variantCache = variantCache
        this.transformInvocation = transformInvocation
    }

    void proceed() {
        transformInvocation.inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput dirInput ->
                variantCache.includeFileContentTypes = dirInput.contentTypes
                variantCache.includeFileScopes = dirInput.scopes

                dirInput.file.eachFileRecurse { File item ->
                    if (AJXUtils.isClassFile(item.name)) {
                        //如果是class文件，复制到build/intermediates/ajx/buildVariant/目录下
                        String path = item.absolutePath
                        String subPath = path.substring(dirInput.file.absolutePath.length())
                        //只要是.class文件就要添加到includeFilePath路径下
                        variantCache.add(item, new File(variantCache.includeFilePath, subPath))
                        //对于项目中的所有的class最终需要复制到build/intermediates/transform/ajx目录下，以便于进行后续的transform操作
                        //这个过程也可以在aspectj处理class的时候使用
                        if (AJXUtils.isAspectClass(item)) {
                            println "~~~~~~~~~~~~directoryInputs collect aspect file:${item.absolutePath}"
                            println "~~~~~~~~~~~~directoryInputs collect aspect file subPath: ${subPath}"
                            //如果是@Aspect注解的class文件，复制到build/intermediates/ajx/buildVariant/aspects目录下
                            File cacheFile = new File(variantCache.aspectPath, subPath)
                            variantCache.add(item, cacheFile)
                        }
                    }
                }
            }

            input.jarInputs.each { JarInput jarInput ->
                JarFile jarFile = new JarFile(jarInput.file)
                //添加jar包中的class到AjxTask的classpath
                Enumeration<JarEntry> entries = jarFile.entries()
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement()
                    String entryName = jarEntry.getName()
                    if (!jarEntry.isDirectory() && AJXUtils.isClassFile(entryName)) {
                        byte[] bytes = ByteStreams.toByteArray(jarFile.getInputStream(jarEntry))
                        if (AJXUtils.isAspectClass(bytes)) {
                            //复制jar包中的被@Aspect注解的class文件
                            File cacheFile = new File(variantCache.aspectPath, entryName)
                            variantCache.add(bytes, cacheFile)
                            println "~~~~~~~~~~~jarInputs collect aspect file:${entryName}"
                        }
                    }
                }

                jarFile.close()
            }
        }
    }
}