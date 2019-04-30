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

    final Property<String> applicationVersion

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
        this.applicationVersion = project.objects.property(String)
        this.mainClassName = project.objects.property(String)
        this.mainJar = project.objects.property(String)
        this.outputTypes = project.objects.listProperty(String)
        this.arguments = project.objects.listProperty(String)
        this.javaOptions = project.objects.listProperty(String)
        this.licenseFile = project.objects.fileProperty()
        this.outputFile = project.objects.fileProperty()
        this.sourceDir = project.objects.directoryProperty()
        this.sourceFiles = project.files()
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
        deployCmdProps.applicationVersion.set(applicationVersion)
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

    @Override
    void setApplicationDescription(String description) {
        this.description.set(description)
    }

    @Override
    void setApplicationName(String name) {
        this.applicationName.set(name)
    }

    @Override
    void setApplicationName(Provider<? extends String> name) {
        this.applicationName.set(name)
    }

    @Override
    void setApplicationVersion(String version) {
        this.applicationVersion.set(version)
    }

    @Override
    void setApplicationVersion(Provider<? extends String> version) {
        this.applicationVersion.set(version)
    }

    @Override
    void setMainClassName(String mainClass) {
        this.mainClassName.set(mainClass)
    }

    @Override
    void setMainClassName(Provider<? extends String> mainClass) {
        this.mainClassName.set(mainClass)
    }

    @Override
    void setMainJar(String name) {
        this.mainJar.set(name)
    }

    @Override
    void setMainJar(Provider<? extends String> name) {
        this.mainJar.set(name)
    }

    @Override
    void setOutputTypes(String... types) {
        this.setOutputTypes(types.toList())
    }

    @Override
    void setOutputTypes(Iterable<? extends String> types) {
        if (!(this instanceof ExtensionAware)) {
            throw new GradleException("DefaultInstallOptions is not extension aware")
        }

        this.outputTypes.set(types)

        types.each { String type ->
            def extThis = this as ExtensionAware
            if (!extThis.extensions.findByName(type)) {
                addExtension(extThis, type)
            }
        }
    }

    @Override
    void setArguments(Iterable<? extends String> args) {
        this.arguments.set(args)
    }

    @Override
    void setJavaOptions(Iterable<? extends String> options) {
        this.javaOptions.set(options)
    }

    @Override
    void setOutputFile(RegularFile file) {
        this.outputFile.set(file)
    }

    @Override
    void setOutputFile(File file) {
        this.outputFile.set(file)
    }

    @Override
    void setOutputFile(String file) {
        this.setOutputFile(project.file(file))
    }

    @Override
    void setSourceDir(File dir) {
        this.sourceDir.set(dir)
    }

    @Override
    void setSourceDir(Directory dir) {
        this.sourceDir.set(dir)
    }

    @Override
    void setSourceFiles(Object... files) {
        this.sourceFiles.setFrom(files)
    }

    @Override
    void setSourceFiles(Iterable<?> files) {
        this.sourceFiles.setFrom(files)
    }

    @Override
    void exe(Action<? super WinOptions> action) {
        executeOsOptionsAction("exe", action)
    }

    @Override
    void msi(Action<? super WinOptions> action) {
        executeOsOptionsAction("msi", action)
    }

    @Override
    void dmg(Action<? super MacOptions> action) {
        executeOsOptionsAction("dmg", action)
    }

    @Override
    void pkg(Action<? super MacOptions> action) {
        executeOsOptionsAction("pkg", action)
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
