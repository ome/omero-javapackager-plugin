package org.openmicroscopy.extensions.implementation

import org.gradle.api.Project
import org.openmicroscopy.InstallerType

class MsiOptions extends WinOptions {
    MsiOptions(Project project) {
        super(InstallerType.MSI, project)
    }
}
