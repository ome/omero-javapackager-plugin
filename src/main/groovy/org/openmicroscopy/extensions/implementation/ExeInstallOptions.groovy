package org.openmicroscopy.extensions.implementation

import org.gradle.api.Project
import org.openmicroscopy.InstallerType

class ExeInstallOptions extends WinOptions {
    ExeInstallOptions(Project project) {
        super(InstallerType.EXE, project)
    }
}
