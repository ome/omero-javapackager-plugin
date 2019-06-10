package org.openmicroscopy.extensions.implementation

import org.gradle.api.Project
import org.openmicroscopy.InstallerType

class MsiInstallOptions extends WinOptions {
    MsiInstallOptions(Project project) {
        super(InstallerType.MSI, project)
    }
}
