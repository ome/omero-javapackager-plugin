package org.openmicroscopy.extensions.implementation

import org.gradle.api.Project
import org.openmicroscopy.InstallerType

class PkgOptions extends MacOptions {
    PkgOptions(Project project) {
        super(InstallerType.PKG, project)
    }
}
