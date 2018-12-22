package com.sososeen09.aspeactj.cache

import com.android.builder.model.AndroidProject
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class AspectJCache {
    Project project
    String cachePath
    String aspectPath
    //for aspectj
    String encoding
    String bootClassPath
    String sourceCompatibility
    String targetCompatibility
    List<String> ajcArgs = new ArrayList<>()


    AspectJCache(Project proj) {
        this.project = proj

        init()
    }

    private void init() {
        //初始化目录，并把之前编译的结果以及配置缓存下来
        cachePath = project.buildDir.absolutePath + File.separator + AndroidProject.FD_INTERMEDIATES + "/ajx"
        aspectPath = cachePath + File.separator + "aspecs"
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    File getCacheDir() {
        return new File(cachePath)
    }

    void reset() {
        FileUtils.deleteDirectory(cacheDir)

        init()
    }
}