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


