package com.sososeen09.aspeactj

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform

class AjxTransform extends Transform{

    @Override
    String getName() {
        return "ajx"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return null
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return null
    }

    @Override
    boolean isIncremental() {
        return false
    }
}