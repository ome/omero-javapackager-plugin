package org.openmicroscopy.extensions.implementation

import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.process.CommandLineArgumentProvider
import org.openmicroscopy.extensions.BaseOsOptions
import org.openmicroscopy.tasks.JavaPackagerDeployDmg
import org.openmicroscopy.tasks.JavaPackagerDeployPkg

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class MacOptions implements BaseOsOptions {

    final Property<Boolean> systemWide

    final Property<Boolean> simple

    final RegularFileProperty icon

    private final Project project

    private final String installerType

    MacOptions(String type, Project project) {
        this.installerType = type
        this.project = project
        this.systemWide = project.objects.property(Boolean)
        this.simple = project.objects.property(Boolean)
        this.icon = project.objects.fileProperty()

        this.systemWide.convention(true)
        this.simple.convention(true)
    }

    @Override
    CommandLineArgumentProvider createCmdArgsProvider() {
        if (installerType == "dmg") {
            def provider = new JavaPackagerDeployDmg(project)
            provider.systemWide.set(systemWide)
            provider.simple.set(simple)
            return provider
        } else if (installerType == "pkg") {
            def provider = new JavaPackagerDeployPkg(project)
            return provider
        } else {
            throw new GradleException("Unknown installer installerType")
        }
    }

    void setIcon(File icon) {
        String fileExtension = FilenameUtils.getExtension(icon.getName())
        if (fileExtension != "icns") {
            throw new GradleException("Only .icn extension supported, supplied: $fileExtension")
        }
        this.icon.set(icon)
    }

}