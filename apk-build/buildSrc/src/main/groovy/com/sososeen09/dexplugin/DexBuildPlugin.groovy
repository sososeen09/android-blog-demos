package com.sososeen09.dexplugin

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.pipeline.TransformTask
import com.android.build.gradle.internal.transforms.DexArchiveBuilderTransform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.execution.TaskExecutionGraphListener

import java.lang.reflect.Field

class DexBuildPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.gradle.addListener(new TaskTimeLisener())
        project.getGradle().getTaskGraph().addTaskExecutionGraphListener(new TaskExecutionGraphListener() {
            @Override
            void graphPopulated(TaskExecutionGraph taskGraph) {
                project.android.applicationVariants.each { ApplicationVariant variant ->
                    for (Task task : taskGraph.getAllTasks()) {
                        if (task instanceof TransformTask && task.name.toLowerCase().contains(variant.name.toLowerCase())) {
                            project.logger.error("==========DexBuildPlugin: task===========" + task.getTransform())
                            // android-gradle-plugin 3.1.2  DexTransform
                            if (((TransformTask) task).getTransform() instanceof DexArchiveBuilderTransform && !(((TransformTask) task).getTransform() instanceof ImmutableDexTransform)) {
                                project.logger.error("==========DexBuildPlugin: DexTransform===========")
                                project.logger.warn("find dex transform. transform class: " + task.transform.getClass() + " . task name: " + task.name)

                                DexArchiveBuilderTransform dexTransform = task.transform
                                ImmutableDexTransform hookDexTransform = new ImmutableDexTransform(project,
                                        variant, dexTransform)
                                project.logger.info("variant name: " + variant.name)

                                Field field = TransformTask.class.getDeclaredField("transform")
                                field.setAccessible(true)
                                field.set(task, hookDexTransform)
                                project.logger.warn("transform class after hook: " + task.transform.getClass())
                                break
                            }
                        }
                    }
                }
            }
        })
    }
}