package com.sososeen09.aspeactj

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.Project

class AjxTask implements ITask {
    Project project
    ArrayList<File> inPath = new ArrayList<>()
    ArrayList<File> aspectPath = new ArrayList<>()
    ArrayList<File> classPath = new ArrayList<>()
    List<String> ajcArgs = new ArrayList<>()
    String encoding
    String bootClassPath
    String sourceCompatibility
    String targetCompatibility

    String outputDir
    String outputJar

    AjxTask(Project proj) {
        project = proj
    }

    @Override
    Object call() throws Exception {
        final def log = project.logger

        //http://www.eclipse.org/aspectj/doc/released/devguide/ajc-ref.html
        //
        // -sourceRoots:
        //  Find and build all .java or .aj source files under any directory listed in DirPaths. DirPaths, like classpath, is a single argument containing a list of paths to directories, delimited by the platform- specific classpath delimiter. Required by -incremental.
        // -inpath:
        //  Accept as source bytecode any .class files in the .jar files or directories on Path. The output will include these classes, possibly as woven with any applicable aspects. Path is a single argument containing a list of paths to zip files or directories, delimited by the platform-specific path delimiter.
        // -classpath:
        //  Specify where to find user class files. Path is a single argument containing a list of paths to zip files or directories, delimited by the platform-specific path delimiter.
        // -aspectPath:
        //  Weave binary aspects from jar files and directories on path into all sources. The aspects should have been output by the same version of the compiler. When running the output classes, the run classpath should contain all aspectpath entries. Path, like classpath, is a single argument containing a list of paths to jar files, delimited by the platform- specific classpath delimiter.
        // -bootClasspath:
        //  Override location of VM's bootClasspath for purposes of evaluating types when compiling. Path is a single argument containing a list of paths to zip files or directories, delimited by the platform-specific path delimiter.
        // -d:
        //  Specify where to place generated .class files. If not specified, Directory defaults to the current working dir.
        // -preserveAllLocals:
        //  Preserve all local variables during code generation (to facilitate debugging).

        def args = [
                "-showWeaveInfo",
                "-encoding", encoding,
                "-source", sourceCompatibility,
                "-target", targetCompatibility,
                "-classpath", classPath.join(File.pathSeparator),
                "-bootclasspath", bootClassPath
        ]


        if (!getInPath().isEmpty()) {
            args << '-inpath'
            args << getInPath().join(File.pathSeparator)
        }

        if (!getAspectPath().isEmpty()) {
            args << '-aspectpath'
            args << getAspectPath().join(File.pathSeparator)
        }

        if (outputDir != null && !outputDir.isEmpty()) {
            args << '-d'
            args << outputDir
        }

        if (outputJar != null && !outputJar.isEmpty()) {
            args << '-outjar'
            args << outputJar
        }

        if (ajcArgs != null && !ajcArgs.isEmpty()) {
            if (!ajcArgs.contains('-Xlint')) {
                args.add('-Xlint:ignore')
            }
            if (!ajcArgs.contains('-warn')) {
                args.add('-warn:none')
            }

            args.addAll(ajcArgs)
        } else {
            args.add('-Xlint:ignore')
            args.add('-warn:none')
        }


        inPath.each { File file ->
            println "~~~~~~~~~~~~~input file: ${file.absolutePath}"
        }

        MessageHandler handler = new MessageHandler(true);
        new Main().run(args as String[], handler);
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
        return null
    }
}
