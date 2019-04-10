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
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.process.CommandLineArgumentProvider

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class JavaPackagerDeployWin implements CommandLineArgumentProvider {

    @Input
    @Optional
    final Property<Boolean> installDirChooser

    @Input
    @Optional
    final Property<Boolean> installUserLevel

    @Input
    @Optional
    final Property<Boolean> addShortcut

    @Input
    @Optional
    final Property<String> copyright

    @Input
    @Optional
    final Property<String> registryVendor

    @Input
    @Optional
    final Property<String> startMenuGroup

    @InputFile
    @Optional
    final RegularFileProperty icon

    JavaPackagerDeployWin(Project project) {
        this.installDirChooser = project.objects.property(Boolean)
        this.installUserLevel = project.objects.property(Boolean)
        this.addShortcut = project.objects.property(Boolean)
        this.copyright = project.objects.property(String)
        this.registryVendor = project.objects.property(String)
        this.startMenuGroup = project.objects.property(String)
        this.icon = project.objects.fileProperty()
    }

    @Override
    Iterable<String> asArguments() {
        List<String> cmd = []

        if (installDirChooser.isPresent()) {
            cmd.add("-BinstalldirChooser=" + installDirChooser.get())
        }

        if (addShortcut.isPresent()) {
            cmd.add("-BshortcutHint=" + addShortcut.get())
        }

        if (copyright.isPresent()) {
            cmd.add("-Bcopyright=" + copyright.get())
        }

        if (startMenuGroup.isPresent()) {
            cmd.add("-Bwin.menuGroup=" + startMenuGroup.get())
        }

        if (registryVendor.isPresent()) {
            cmd.add("-Bvendor=" + registryVendor.get())
        }

        return cmd
    }

}
