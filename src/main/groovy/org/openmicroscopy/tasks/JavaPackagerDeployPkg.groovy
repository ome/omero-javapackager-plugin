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


import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.process.CommandLineArgumentProvider

class JavaPackagerDeployPkg implements CommandLineArgumentProvider {

    final Property<String> signingKeyDeveloperIdInstaller

    JavaPackagerDeployPkg(Project project) {
        signingKeyDeveloperIdInstaller = project.objects.property(String)
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    @Override
    Iterable<String> asArguments() {
        List cmd = []
        if (signingKeyDeveloperIdInstaller.isPresent()) {
            cmd.add("-Bmac.signing-key-developer-id-installer=${signingKeyDeveloperIdInstaller.get()}")
        } else {
            cmd.add("-nosign")
        }
        return cmd
    }

}
