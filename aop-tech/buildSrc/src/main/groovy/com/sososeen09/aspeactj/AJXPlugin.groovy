package com.sososeen09.aspeactj

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.sososeen09.aspeactj.internal.TaskTimeListener
import org.gradle.api.Plugin
import org.gradle.api.Project

class AJXPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.repositories {
            google()
            mavenCentral()
        }

        project.dependencies {
            api 'org.aspectj:aspectjrt:1.8.10'
        }

        if (project.plugins.hasPlugin(AppPlugin)) {
            project.gradle.addListener(new TaskTimeListener())
            AppExtension android = project.extensions.getByType(AppExtension)
            android.registerTransform(new AJXTransform(project))
        }
    }
}