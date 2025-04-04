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
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.process.CommandLineArgumentProvider
import org.openmicroscopy.InstallerType
import org.openmicroscopy.extensions.FileUtils
import org.openmicroscopy.extensions.InstallOsOptions
import org.openmicroscopy.tasks.JavaPackagerDeployWin

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
abstract class WinOptions implements InstallOsOptions {

    final Property<Boolean> installDirChooser

    final Property<Boolean> installUserLevel

    final Property<Boolean> addShortcut

    final Property<String> copyright

    final Property<String> registryVendor

    final Property<String> startMenuGroup

    final RegularFileProperty icon

    private final Project project

    private final InstallerType type

    WinOptions(InstallerType type, Project project) {
        this.type = type
        this.project = project
        this.installDirChooser = project.objects.property(Boolean)
        this.installUserLevel = project.objects.property(Boolean)
        this.addShortcut = project.objects.property(Boolean)
        this.copyright = project.objects.property(String)
        this.registryVendor = project.objects.property(String)
        this.startMenuGroup = project.objects.property(String)
        this.icon = project.objects.fileProperty()

        this.addShortcut.convention(true)
        this.installUserLevel.convention(true)
        this.installDirChooser.convention(true)
    }

    @Override
    CommandLineArgumentProvider createCmdArgsProvider() {
        def provider = new JavaPackagerDeployWin(project)
        provider.installDirChooser.set(this.installDirChooser)
        provider.installUserLevel.set(this.installUserLevel)
        provider.addShortcut.set(this.addShortcut)
        provider.copyright.set(this.copyright)
        provider.registryVendor.set(this.registryVendor)
        provider.startMenuGroup.set(this.startMenuGroup)
        return provider
    }

    void setInstallDirChooser(boolean installDirChooser) {
        this.installDirChooser.set(installDirChooser)
    }

    void setInstallUserLevel(boolean installUserLevel) {
        this.installUserLevel.set(installUserLevel)
    }

    void setAddShortcut(boolean addShortcut) {
        this.addShortcut.set(addShortcut)
    }

    void setCopyright(String copyright) {
        this.copyright.set(copyright)
    }

    void setRegistryVendor(String registryVendor) {
        this.registryVendor.set(registryVendor)
    }

    void setStartMenuGroup(String startMenuGroup) {
        this.startMenuGroup.set(startMenuGroup)
    }

    void setIcon(File icon) {
        String fileExtension = FileUtils.getExtension(icon)
        if (fileExtension != "ico") {
            throw new GradleException("Only .ico extension supported, supplied: $fileExtension")
        }
        this.icon.set(icon)
    }

}

