package com.sososeen09.aspeactj

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.pipeline.TransformTask
import com.google.common.collect.ImmutableSet
import com.sososeen09.aspeactj.cache.VariantCache
import com.sososeen09.aspeactj.process.AJXFileProcess
import com.sososeen09.aspeactj.process.AJXTaskProcess
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
        //VariantCache 就是保存一些跟当前variant相关的一些缓存，以及在支持增量编译的情况下存储一些信息
        VariantCache variantCache = new VariantCache(ajxProcedure.project, ajxProcedure.ajxCache, transformTask.variantName)

        if (transformInvocation.isIncremental()) {
            //TODO 增量
            print("====================增量编译=================")
        }else {
            print("====================非增量编译=================")
            //非增量,需要删除输出目录
            transformInvocation.outputProvider.deleteAll()
            variantCache.reset()

            AJXFileProcess ajxFileProcess = new AJXFileProcess(project, variantCache, transformInvocation)
            ajxFileProcess.proceed()
            AJXTaskProcess ajxTaskProcess = new AJXTaskProcess(project, variantCache, transformInvocation)
            ajxTaskProcess.proceed()
        }

    }
}