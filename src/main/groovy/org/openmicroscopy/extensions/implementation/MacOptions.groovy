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
import org.apache.commons.io.FilenameUtils
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.openmicroscopy.InstallerType
import org.openmicroscopy.extensions.InstallOsOptions

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
abstract class MacOptions implements InstallOsOptions {

    final RegularFileProperty icon

    protected final Project project

    protected final String installerType

    MacOptions(InstallerType type, Project project) {
        this.installerType = type
        this.project = project

        this.icon = project.objects.fileProperty()
    }

    void setIcon(File icon) {
        String fileExtension = FilenameUtils.getExtension(icon.getName())
        if (fileExtension != "icns") {
            throw new GradleException("Only .icn extension supported, supplied: $fileExtension")
        }
        this.icon.set(icon)
    }

}