package com.sososeen09.aspeactj

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.pipeline.TransformTask
import com.google.common.collect.ImmutableSet
import com.sososeen09.aspeactj.cache.VariantCache
import com.sososeen09.aspeactj.process.AjxFileProcess
import com.sososeen09.aspeactj.process.AjxTaskProcess
import org.gradle.api.Project


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
        //VariantCache 就是保存一些跟当前variant相关的一些缓存
        VariantCache variantCache = new VariantCache(ajxProcedure.project, ajxProcedure.ajxCache, transformTask.variantName)

        AjxFileProcess ajxFileProcess = new AjxFileProcess(project, variantCache, transformInvocation)

        ajxFileProcess.proceed()

        AjxTaskProcess ajxTaskProcess = new AjxTaskProcess(project, variantCache, transformInvocation)

        ajxTaskProcess.proceed()
    }
}