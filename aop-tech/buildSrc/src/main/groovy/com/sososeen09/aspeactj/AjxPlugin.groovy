package com.sososeen09.aspeactj

import com.android.build.gradle.AppPlugin
import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class AjxPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def config = new AjxConfig(project)

        project.repositories {
            google()
            mavenCentral()
        }

        project.dependencies {
            api 'org.aspectj:aspectjrt:1.8.10'
        }

        final def log = project.logger

        config.variants.all { variant ->
            if (!variant.buildType.isDebuggable()) {
                log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
                return;
            }

            JavaCompile javaCompile = (variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile) as JavaCompile
            javaCompile.doLast {
                String[] args = ["-showWeaveInfo",
                                 "-1.8",
                                 "-inpath", javaCompile.destinationDir.toString(),
                                 "-aspectpath", javaCompile.classpath.asPath,
                                 "-d", javaCompile.destinationDir.toString(),
                                 "-classpath", javaCompile.classpath.asPath,
                                 "-bootclasspath", config.bootClasspath.join(File.pathSeparator)]
                log.debug "ajc args: " + Arrays.toString(args)

                MessageHandler handler = new MessageHandler(true);
                new Main().run(args, handler);
                for (IMessage message : handler.getMessages(null, true)) {
                    switch (message.getKind()) {
                        case IMessage.ABORT:
                        case IMessage.ERROR:
                        case IMessage.FAIL:
                            log.error message.message, message.thrown
                            break;
                        case IMessage.WARNING:
                            log.warn message.message, message.thrown
                            break;
                        case IMessage.INFO:
                            log.info message.message, message.thrown
                            break;
                        case IMessage.DEBUG:
                            log.debug message.message, message.thrown
                            break;
                    }
                }
            }
        }

//        project.afterEvaluate {
//            config.variants.all { variant ->
//                if (!variant.buildType.isDebuggable()) {
//                    log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
//                    return;
//                }
//                JavaCompile javaCompile = (variant.hasProperty('javaCompiler') ? variant.javaCompiler : variant.javaCompile) as JavaCompile
//                def ajxTask = project.tasks.create("ajxTask", AjxTask)
//                ajxTask.sourceCompatibility = javaCompile.sourceCompatibility
//                ajxTask.targetCompatibility = javaCompile.targetCompatibility
//                ajxTask.encoding = javaCompile.options.encoding
//                ajxTask.aspectPath = javaCompile.classpath.asList()
//                ajxTask.outputDir = javaCompile.destinationDir.absolutePath
//                ajxTask.classpath = javaCompile.classpath
//                ajxTask.bootClasspath = config.bootClasspath.join(File.pathSeparator)
//                ajxTask.inPath=javaCompile.destinationDir.listFiles()
//
//                ajxTask.dependsOn javaCompile
//                javaCompile.finalizedBy ajxTask
//            }
//        }

        if (project.plugins.hasPlugin(AppPlugin)) {
            project.gradle.addListener(new TaskTimeListener())
        }
    }
}