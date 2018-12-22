package com.sososeen09.aspeactj.process

import com.android.build.api.transform.*
import com.google.common.io.ByteStreams
import com.sososeen09.aspeactj.AspectJUtils
import com.sososeen09.aspeactj.cache.VariantCache
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 * 增量 解析class文件和相关的jar包
 */
class AspectJFileIncrementalProcess {
    Project project
    VariantCache variantCache
    TransformInvocation transformInvocation

    AspectJFileIncrementalProcess(Project project, VariantCache variantCache, TransformInvocation transformInvocation) {
        this.project = project
        this.variantCache = variantCache
        this.transformInvocation = transformInvocation
    }

    void proceed() {
        transformInvocation.inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput dirInput ->
                variantCache.includeFileContentTypes = dirInput.contentTypes
                variantCache.includeFileScopes = dirInput.scopes

                dirInput.changedFiles.each { File file, Status status ->
                    if (AspectJUtils.isClassFile(item.name)) {
                        //如果是class文件，复制到build/intermediates/ajx/buildVariant/目录下
                        String path = item.absolutePath
                        String subPath = path.substring(dirInput.file.absolutePath.length())
                        //只要是.class文件就要添加到includeFilePath路径下
                        File cacheFile = new File(variantCache.includeFilePath, subPath)
                        switch (status) {
                            case Status.REMOVED:
                                FileUtils.deleteQuietly(cacheFile)
                                break
                            case Status.CHANGED:
                                FileUtils.deleteQuietly(cacheFile)
                                variantCache.add(file, cacheFile)
                                break
                            case Status.ADDED:
                                variantCache.add(item, cacheFile)
                                break
                            default:
                                break
                        }

                        //对于项目中的所有的class最终需要复制到build/intermediates/transform/ajx目录下，以便于进行后续的transform操作
                        //这个过程也可以在aspectj处理class的时候使用
                        if (AspectJUtils.isAspectClass(item)) {
                            println "~~~~~~~~~~~~directoryInputs collect aspect file:${item.absolutePath}"
                            println "~~~~~~~~~~~~directoryInputs collect aspect file subPath: ${subPath}"
                            //如果是@Aspect注解的class文件，复制到build/intermediates/ajx/buildVariant/aspects目录下
                            File cacheAspectFile = new File(variantCache.aspectPath, subPath)
                            switch (status) {
                                case Status.REMOVED:
                                    FileUtils.deleteQuietly(cacheAspectFile)
                                    break
                                case Status.CHANGED:
                                    FileUtils.deleteQuietly(cacheAspectFile)
                                    variantCache.add(item, cacheAspectFile)
                                    break
                                case Status.ADDED:
                                    variantCache.add(item, cacheAspectFile)
                                    break
                                default:
                                    break
                            }
                        }
                    }
                }
            }

            input.jarInputs.each { JarInput jarInput ->

                if (jarInput.status != Status.NOTCHANGED) {
                    JarFile jarFile = new JarFile(jarInput.file)
                    //添加jar包中的class到AjxTask的classpath
                    Enumeration<JarEntry> entries = jarFile.entries()
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement()
                        String entryName = jarEntry.getName()
                        if (!jarEntry.isDirectory() && AspectJUtils.isClassFile(entryName)) {
                            byte[] bytes = ByteStreams.toByteArray(jarFile.getInputStream(jarEntry))
                            File cacheFile = new File(variantCache.aspectPath, entryName)
                            if (AspectJUtils.isAspectClass(bytes)) {
                                //复制jar包中的被@Aspect注解的class文件
                                println "~~~~~~~~~~~~~~~~~collect aspect file from JAR:${entryName}"

                                if (jarInput.status == Status.REMOVED) {
                                    FileUtils.deleteQuietly(cacheFile)
                                } else if (jarInput.status == Status.CHANGED) {
                                    FileUtils.deleteQuietly(cacheFile)
                                    variantCache.add(bytes, cacheFile)
                                } else if (jarInput.status == Status.ADDED) {
                                    variantCache.add(bytes, cacheFile)
                                }
                            }
                        }
                    }

                    jarFile.close()


                    println "~~~~~~~changed file::${jarInput.status.name()}::${jarInput.file.absolutePath}"

                    String filePath = jarInput.file.absolutePath
                    File outputJar = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)

                    if (jarInput.status == Status.REMOVED) {
                        FileUtils.deleteQuietly(outputJar)
                    } else if (jarInput.status == Status.ADDED) {
                        //TODO 处理新增的情况
                    } else if (jarInput.status == Status.CHANGED) {
                        FileUtils.deleteQuietly(outputJar)
                    }
                }

            }
        }
    }
}