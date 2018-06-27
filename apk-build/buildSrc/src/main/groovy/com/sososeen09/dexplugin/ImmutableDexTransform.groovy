package com.sososeen09.dexplugin

import com.android.annotations.NonNull
import com.android.build.api.transform.*
import com.android.build.gradle.internal.transforms.DexArchiveBuilderTransform
import com.google.common.base.Joiner
import org.gradle.api.Project
/**
 * 从3.0之后 DexTransform在构建的时候用不到了
 */
class ImmutableDexTransform extends Transform {

    public static final String TASK_WORK_DIR = "keep_dex"

    private static final Joiner PATH_JOINER = Joiner.on(File.separatorChar)

    Project project

    File classPreDir

    File baseDexDir

    String varName

    String varDirName

    def variant

    DexArchiveBuilderTransform dexTransform


    ImmutableDexTransform(Project project, def variant, DexArchiveBuilderTransform dexTransform) {
        this.dexTransform = dexTransform
        this.project = project
        this.variant = variant
        this.varName = variant.name.capitalize()
        this.varDirName = variant.getDirName()
    }

    public void initFileEnv(TransformOutputProvider outputProvider) {
        classPreDir = getDirInWorkDir("class_pre")
        baseDexDir = getDirInWorkDir("base_dex")

        classPreDir.mkdirs()
        baseDexDir.mkdirs()

        FileOperation.cleanDir(classPreDir)
        FileOperation.cleanDir(baseDexDir)
    }

    private File getDirInWorkDir(String name) {
        return new File(PATH_JOINER.join(project.projectDir,
                TinkerPatchPlugin.TINKER_INTERMEDIATES,
                TASK_WORK_DIR,
                name,
                varDirName)
        )
    }


    @NonNull
    @Override
    public Set<QualifiedContent.ContentType> getOutputTypes() {
        return dexTransform.getOutputTypes()
    }

    @NonNull
    @Override
    public Collection<File> getSecondaryFileInputs() {
        return dexTransform.getSecondaryFileInputs()
    }

    @NonNull
    @Override
    public Collection<File> getSecondaryDirectoryOutputs() {
        return dexTransform.getSecondaryDirectoryOutputs()
    }

    @NonNull
    @Override
    public Map<String, Object> getParameterInputs() {
        return dexTransform.getParameterInputs()
    }

    @Override
    String getName() {
        return dexTransform.getName()
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return dexTransform.getInputTypes()
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return dexTransform.getScopes()
    }

    @Override
    boolean isIncremental() {
        return dexTransform.isIncremental()
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, IOException, InterruptedException {
        dexTransform.transform(transformInvocation)
        // TransformOutputProviderImpl
        def outputProvider = transformInvocation.getOutputProvider()
        //dex的输出目录
        File outputDir = outputProvider.getContentLocation("main", dexTransform.getOutputTypes(), dexTransform.getScopes(), Format.DIRECTORY);
        println("===执行dexTransform的dex输出目录: ${project.projectDir.toPath().relativize(outputDir.toPath())}")

        if (outputDir.exists()) {
            println("===执行dexTransform后dex输出目录不是空的: ${project.projectDir.toPath().relativize(outputDir.toPath())}")
            outputDir.listFiles().each {
                println("===执行dexTransform后: ${it.name}")
            }
        }
    }
}