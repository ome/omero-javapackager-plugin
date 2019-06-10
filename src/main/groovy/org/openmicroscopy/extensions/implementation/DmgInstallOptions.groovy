package org.openmicroscopy.extensions.implementation

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.process.CommandLineArgumentProvider
import org.openmicroscopy.InstallerType
import org.openmicroscopy.tasks.JavaPackagerDeployDmg

class DmgInstallOptions extends MacOptions {

    final Property<Boolean> simple

    final Property<Boolean> systemWide

    DmgInstallOptions(Project project) {
        super(InstallerType.DMG, project)

        this.simple = project.objects.property(Boolean)
        this.systemWide = project.objects.property(Boolean)

        this.simple.convention(true)
        this.systemWide.convention(true)
    }

    @Override
    CommandLineArgumentProvider createCmdArgsProvider() {
        def provider = new JavaPackagerDeployDmg(project)
        provider.systemWide.set(systemWide)
        provider.simple.set(simple)
        return provider
    }
}
