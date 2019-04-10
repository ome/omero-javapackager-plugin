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
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.process.CommandLineArgumentProvider

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class JavaPackagerDeployDmg implements CommandLineArgumentProvider {

    @Input
    @Optional
    final Property<Boolean> simple

    @Input
    @Optional
    final Property<Boolean> systemWide

    @InputFile
    @Optional
    final RegularFileProperty icon

    JavaPackagerDeployDmg(Project project) {
        this.simple = project.objects.property(Boolean)
        this.systemWide = project.objects.property(Boolean)
        this.icon = project.objects.fileProperty()
    }

    @Override
    Iterable<String> asArguments() {
        List<String> cmd = []

        cmd.add("-nosign")

        if (simple.isPresent()) {
            cmd.add("-Bmac.dmg.simple=" + String.valueOf(simple.get()))
        }

        if (systemWide.isPresent()) {
            cmd.add("-BsystemWide=" + String.valueOf(systemWide.get()))
        }

        return cmd
    }

}
