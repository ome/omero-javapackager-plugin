package org.openmicroscopy.tasks

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.process.CommandLineArgumentProvider

import java.nio.file.Path

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class JavaPackagerDeploy implements CommandLineArgumentProvider {

    @Input
    @Optional
    final Property<String> nativeType

    @Input
    @Optional
    final Property<String> applicationDescription

    @Input
    @Optional
    final Property<String> applicationName

    @Input
    @Optional
    final Property<String> mainJar

    @Input
    final Property<String> outputFileName

    @Input
    final Property<String> mainClass

    @Input
    @Optional
    final ListProperty<String> arguments

    @Input
    @Optional
    final ListProperty<String> jvmOptions

    @Input
    @Optional
    final MapProperty<String, String> applicationArguments

    @InputFile
    @Optional
    final RegularFileProperty icon

    @OutputDirectory
    final DirectoryProperty outputDir

    @InputDirectory
    final DirectoryProperty srcDir

    @InputFiles
    final ConfigurableFileCollection srcFiles

    JavaPackagerDeploy(Project project) {
        nativeType = project.objects.property(String)
        applicationDescription = project.objects.property(String)
        applicationName = project.objects.property(String)
        mainJar = project.objects.property(String)
        outputFileName = project.objects.property(String)
        mainClass = project.objects.property(String)
        arguments = project.objects.listProperty(String)
        jvmOptions = project.objects.listProperty(String)
        applicationArguments = project.objects.mapProperty(String, String)
        icon = project.objects.fileProperty()
        outputDir = project.objects.directoryProperty()
        srcDir = project.objects.directoryProperty()
        srcFiles = project.objects.fileCollection()
    }

    @Override
    Iterable<String> asArguments() {
        List<String> args = []

        // Bundle options
        if (icon.isPresent()) {
            args.add("-Bicon=" + icon.asFile.get())
        }

        if (mainJar.isPresent()) {
            Path mainJarPath = relativeSrcFiles.find { it.fileName.toString() == mainJar.get() }
            if (mainJarPath) {
                args.add("-BmainJar=" + mainJarPath)
            }
        }

        if (nativeType.isPresent()) {
            Collections.addAll(args, "-native", nativeType.get())
        }

        Map<String, String> bundleArgs = applicationArguments.get()
        if (!bundleArgs.isEmpty()) {
            def appArgs = new StringBuilder()
            bundleArgs.each { entry ->
                appArgs.append("$entry.key=$entry.value").append(" ")
            }
            args.add("-Barguments=\"" + appArgs.toString().trim() + "\"")
        }

        List<String> jvmOptionsList = jvmOptions.get()
        if (!jvmOptionsList.isEmpty()) {
            jvmOptionsList.each { String option ->
                args.add("-BjvmOptions=" + option)
            }
        }

        if (applicationName.isPresent()) {
            Collections.addAll(args, "-name", applicationName.get())
        }

        if (applicationDescription.isPresent()) {
            Collections.addAll(args, "-description", applicationDescription.get())
        }

        // Unnamed arguments
        List<String> unnamedArgs = arguments.get()
        if (!unnamedArgs.isEmpty()) {
            unnamedArgs.each { String argument ->
                Collections.addAll(args, "-argument", argument)
            }
        }

        // Non optional
        Collections.addAll(args, "-appclass", mainClass.get())
        Collections.addAll(args, "-outdir", String.valueOf(outputDir.asFile.get()))
        Collections.addAll(args, "-outfile", outputFileName.get())
        Collections.addAll(args, "-srcdir", String.valueOf(srcDir.asFile.get()))

        // Append relative src files, relative to the srcDir
        List relativeSrcFiles = getRelativeSrcFiles()
        relativeSrcFiles.each { Path path ->
            Collections.addAll(args, "-srcfiles", String.valueOf(path))
        }

        // Verbose output
        args.add("-v")

        return args
    }

    private List<Path> getRelativeSrcFiles() {
        def srcFilesList = srcFiles.files
        if (srcFilesList.isEmpty()) {
            return []
        }

        def srcDirPath = srcDir.asFile.get().toPath()
        srcFilesList.collect { File file ->
            srcDirPath.relativize(file.toPath())
        }
    }

}
