package org.openmicroscopy.extensions.implementation

import org.gradle.api.Project
import org.openmicroscopy.InstallerType

class ExeOptions extends WinOptions {
    ExeOptions(Project project) {
        super(InstallerType.EXE, project)
    }
}
