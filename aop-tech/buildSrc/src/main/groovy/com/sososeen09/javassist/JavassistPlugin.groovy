package com.sososeen09.javassist

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class JavassistPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (project.plugins.hasPlugin(AppPlugin)) {
            def log = project.logger
            log.error "========================";
            log.error "Javassist开始修改Class!";
            log.error "========================";

            AppExtension android = project.extensions.getByType(AppExtension)
            android.registerTransform(new JavassistTransform(project))
        }
    }
}