package com.sososeen09.aspeactj.cache

import com.android.build.api.transform.QualifiedContent
import com.android.builder.model.AndroidProject
import com.google.common.collect.ImmutableSet
import org.gradle.api.Project
/**
 * 该类用户管理Variant相关的缓存，如aspect class文件目录
 */
class VariantCache {
    Project project
    AJXCache ajxCache
    String variantName

    String cachePath
    String aspectPath

    Set<QualifiedContent.ContentType> contentTypes = ImmutableSet.<QualifiedContent.ContentType>of(QualifiedContent.DefaultContentType.CLASSES)
    Set<QualifiedContent.Scope> scopes = ImmutableSet.<QualifiedContent.Scope>of(QualifiedContent.Scope.EXTERNAL_LIBRARIES)

    VariantCache(Project proj, AJXCache cache, String variantName) {
        this.project = proj
        this.variantName = variantName
        this.ajxCache = cache
        this.ajxCache.put(variantName, this)

        init()
    }

    private void init() {
        cachePath = project.buildDir.absolutePath + File.separator + AndroidProject.FD_INTERMEDIATES + "/ajx/" + variantName
        aspectPath = cachePath + File.separator + "aspecs"

        if (!aspectDir.exists()) {
            aspectDir.mkdirs()
        }

        if (!getCacheDir().exists()) {
            getCacheDir().mkdirs()
        }
    }

    File getCacheDir() {
        return new File(cachePath)
    }

    File getAspectDir() {
        return new File(aspectPath)
    }

}