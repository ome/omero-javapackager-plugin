package org.openmicroscopy.extensions.implementation

import org.gradle.api.Project
import org.gradle.process.CommandLineArgumentProvider
import org.openmicroscopy.InstallerType
import org.openmicroscopy.tasks.JavaPackagerDeployPkg

class PkgInstallOptions extends MacOptions {
    PkgInstallOptions(Project project) {
        super(InstallerType.PKG, project)
    }

    @Override
    CommandLineArgumentProvider createCmdArgsProvider() {
        return new JavaPackagerDeployPkg(project)
    }
}
