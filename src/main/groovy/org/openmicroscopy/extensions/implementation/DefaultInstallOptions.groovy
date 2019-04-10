package org.openmicroscopy.extensions.implementation

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.process.CommandLineArgumentProvider
import org.openmicroscopy.extensions.BaseOsOptions
import org.openmicroscopy.extensions.InstallOptions
import org.openmicroscopy.tasks.JavaPackagerDeploy

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class DefaultInstallOptions implements InstallOptions {

    final Property<String> description

    final Property<String> applicationName

    final Property<String> mainClassName

    final Property<String> mainJar

    final ListProperty<String> outputTypes

    final ListProperty<String> arguments

    final ListProperty<String> javaOptions

    final RegularFileProperty licenseFile

    final RegularFileProperty outputFile

    final DirectoryProperty sourceDir

    final ConfigurableFileCollection sourceFiles

    private final String name

    private final Project project

    DefaultInstallOptions(String name, Project project) {
        this.name = name
        this.project = project
        this.description = project.objects.property(String)
        this.applicationName = project.objects.property(String)
        this.mainClassName = project.objects.property(String)
        this.mainJar = project.objects.property(String)
        this.outputTypes = project.objects.listProperty(String)
        this.arguments = project.objects.listProperty(String)
        this.javaOptions = project.objects.listProperty(String)
        this.licenseFile = project.objects.fileProperty()
        this.outputFile = project.objects.fileProperty()
        this.sourceDir = project.objects.directoryProperty()
        this.sourceFiles = project.objects.fileCollection()
    }

    Iterable<CommandLineArgumentProvider> createCmdArgProviders(String outputType) {
        List<CommandLineArgumentProvider> cmdArgProviders = []

        BaseOsOptions osOptions = extensionContainer.getByName(outputType) as BaseOsOptions
        cmdArgProviders.add(osOptions.createCmdArgsProvider())

        // Add self
        JavaPackagerDeploy deployCmdProps = new JavaPackagerDeploy(project)
        deployCmdProps.nativeType.set(outputType)
        deployCmdProps.icon.set(osOptions.icon)
        deployCmdProps.mainClass.set(mainClassName)
        deployCmdProps.mainJar.set(mainJar)
        deployCmdProps.arguments.set(arguments)
        deployCmdProps.jvmOptions.set(javaOptions)
        deployCmdProps.applicationName.set(applicationName)
        deployCmdProps.srcDir.set(sourceDir)
        deployCmdProps.srcFiles.from(sourceFiles)
        deployCmdProps.outputDir.set(getOutputDir())
        deployCmdProps.outputFileName.set(getOutputFileName())

        // Add general command line arguments
        cmdArgProviders.add(deployCmdProps)
        cmdArgProviders
    }

    @Override
    String getName() {
        return name
    }

    Provider<Directory> getOutputDir() {
        return this.outputFile.flatMap { RegularFile regularFile ->
            DirectoryProperty property = project.objects.directoryProperty()
            property.set(regularFile.asFile.getParentFile())
            property
        }
    }

    Provider<String> getOutputFileName() {
        return this.outputFile.map { RegularFile regularFile ->
            regularFile.asFile.name
        }
    }

    void exe(Action<? super WinOptions> action) {
        executeOsOptionsAction("exe", action)
    }

    void msi(Action<? super WinOptions> action) {
        executeOsOptionsAction("msi", action)
    }

    void dmg(Action<? super MacOptions> action) {
        executeOsOptionsAction("dmg", action)
    }

    void pkg(Action<? super MacOptions> action) {
        executeOsOptionsAction("pkg", action)
    }

    void setApplicationName(Provider<? extends String> name) {
        this.applicationName.set(name)
    }

    void setApplicationName(String name) {
        this.applicationName.set(name)
    }

    void setMainClassName(String mainClass) {
        this.mainClassName.set(mainClass)
    }

    void setOutputTypes(String... types) {
        this.setOutputTypes(types.toList())
    }

    void setOutputTypes(Iterable<? extends String> types) {
        this.outputTypes.set(types)
        if (!(this instanceof ExtensionAware)) {
            return
        }

        def extThis = this as ExtensionAware
        types.each { String type ->
            if (!extThis.extensions.findByName(type)) {
                addExtension(extThis, type)
            }
        }
    }

    void setArguments(Iterable<? extends String> args) {
        this.arguments.set(args)
    }

    void setJavaOptions(Iterable<? extends String> options) {
        this.javaOptions.set(options)
    }

    void setOutputFile(RegularFile file) {
        this.outputFile.set(file)
    }

    void setOutputFile(File file) {
        this.outputFile.set(file)
    }

    void setSourceDir(File dir) {
        this.sourceDir.set(dir)
    }

    void setSourceDir(Directory dir) {
        this.sourceDir.set(dir)
    }

    void setMainJar(Provider<? extends String> name) {
        this.mainJar.set(name)
    }

    void setMainJar(String name) {
        this.mainJar.set(name)
    }

    void setSourceFiles(Object... files) {
        this.sourceFiles.setFrom(files)
    }

    void setSourceFiles(Iterable<?> files) {
        this.sourceFiles.setFrom(files)
    }

    private <T> void executeOsOptionsAction(String methodName, Action<T> action) {
        T options = extensionContainer.findByName(methodName) as T
        if (!options) {
            throw new GradleException("You have to add '${methodName}' as an outputType " +
                    "before attempting to configure these install options")
        }
        action.execute(options)
    }

    private def addExtension(ExtensionAware extContainer, String type) {
        switch (type) {
            case "exe":
            case "msi":
                extContainer.extensions.create(type, WinOptions, type, project)
                break
            case "dmg":
            case "pkg":
                extContainer.extensions.create(type, MacOptions, type, project)
                break
        }
    }

}
