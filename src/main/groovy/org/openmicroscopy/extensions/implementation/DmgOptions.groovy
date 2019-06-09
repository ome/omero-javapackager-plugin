package org.openmicroscopy.extensions.implementation

import org.gradle.api.Project
import org.openmicroscopy.InstallerType

class DmgOptions extends MacOptions {
    DmgOptions(Project project) {
        super(InstallerType.DMG, project)
    }
}
