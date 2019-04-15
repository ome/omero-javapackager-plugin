/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2019 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * ------------------------------------------------------------------------------
 */
package org.openmicroscopy.tasks

import groovy.transform.CompileStatic
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
import org.gradle.util.GradleVersion

import java.nio.file.Path

/**
 * Creates task for running javapackager command line tool
 * <p>
 * Example:
 * <pre class='autoTested'>
 * jar javaPackage(type: Exec) {*
 *     def argProvider = new JavaPackagerDeploy(getProject())
 *     argProvider.nativeType.set("dmg")
 *     argProvider.applicationName.set("OMERO.insight")
 *     argProvider.outputFileName.set("OMERO.insight")
 *     argProvider.mainClass.set("org.openmicroscopy.shoola.Main")
 *     argProvider.outputDir.set(layout.buildDirectory.dir("bundles"))
 *     argProvider.srcDir.set(layout.buildDirectory.dir("install/omero-insight-shadow"))
 *     argProvider.icon.set(layout.projectDirectory.file("icons/omeroinsight.icns"))
 *     argProvider.srcFiles.from(fileTree('build/install/omero-insight-shadow') { include '**\/*.*' })
 *     argProvider.arguments.add("container.xml")
 *
 *     argumentProviders.add(argProvider)
 *     executable("javapackager")
 *     args("-deploy")
 *}* </pre>
 * <p>
 */
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
        srcFiles = project.files()
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
