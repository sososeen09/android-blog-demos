package com.sososeen09.aspeactj

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BasePlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DomainObjectCollection
import org.gradle.api.GradleException
import org.gradle.api.Project

class AspectJConfig {

    private final Project project
    private final boolean hasAppPlugin
    private final boolean hasLibPlugin
    private final BasePlugin plugin

    AspectJConfig(Project project) {
        this.project = project
        this.hasAppPlugin = project.plugins.hasPlugin(AppPlugin)
        this.hasLibPlugin = project.plugins.hasPlugin(LibraryPlugin)

        if (!hasAppPlugin && !hasLibPlugin) {
            throw new GradleException("android-aspjectjx: The 'com.android.application' or 'com.android.library' plugin is required.")
        }

        //AppPlugin 和LibraryPlugin 均继承自BasePlugin
        this.plugin = project.plugins.getPlugin(hasAppPlugin ? AppPlugin : LibraryPlugin)
    }

    /**
     * Return all variants.
     *
     * @return Collection of variants.
     */
    DomainObjectCollection<BaseVariant> getVariants() {
        return hasAppPlugin ? project.android.applicationVariants : project.android.libraryVariants
    }

    /**
     * Return boot classpath.
     * @return Collection of classes.
     */
    List<File> getBootClasspath() {
        if (project.android.hasProperty('bootClasspath')) {
            return project.android.bootClasspath
        } else {
            return plugin.runtimeJarList
        }
    }
}
