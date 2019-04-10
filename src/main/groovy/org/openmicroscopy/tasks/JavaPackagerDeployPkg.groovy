package org.openmicroscopy.tasks


import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.process.CommandLineArgumentProvider

class JavaPackagerDeployPkg implements CommandLineArgumentProvider {

    final Property<String> signingKeyDeveloperIdInstaller

    JavaPackagerDeployPkg(Project project) {
        signingKeyDeveloperIdInstaller = project.objects.property(String)
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    @Override
    Iterable<String> asArguments() {
        List cmd = []
        if (signingKeyDeveloperIdInstaller.isPresent()) {
            cmd.add("-Bmac.signing-key-developer-id-installer=${signingKeyDeveloperIdInstaller.get()}")
        } else {
            cmd.add("-nosign")
        }
        return cmd
    }

}
