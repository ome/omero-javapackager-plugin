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
package org.openmicroscopy

import groovy.transform.CompileStatic
import org.gradle.internal.os.OperatingSystem

@CompileStatic
class Platform {

    private static final List<InstallerType> installerTypes = OperatingSystem.current().isWindows() ?
            [InstallerType.EXE, InstallerType.MSI] : OperatingSystem.current().isMacOsX() ?
            [InstallerType.DMG, InstallerType.PKG] : []

    private static final String validIconExtension = OperatingSystem.current().isWindows() ?
            "ico" : OperatingSystem.current().isMacOsX() ?
            "icns" : ""

    static final List<InstallerType> getInstallerTypes() {
        return installerTypes
    }

    static final List<String> getInstallerTypesAsString() {
        return installerTypes.collect { it.type }
    }

    static String getIconExtension() {
        return validIconExtension
    }

}
