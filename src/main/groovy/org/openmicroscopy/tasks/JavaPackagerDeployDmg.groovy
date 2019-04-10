package org.openmicroscopy.tasks

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.process.CommandLineArgumentProvider

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class JavaPackagerDeployDmg implements CommandLineArgumentProvider {

    @Input
    @Optional
    final Property<Boolean> simple

    @Input
    @Optional
    final Property<Boolean> systemWide

    @InputFile
    @Optional
    final RegularFileProperty icon

    JavaPackagerDeployDmg(Project project) {
        this.simple = project.objects.property(Boolean)
        this.systemWide = project.objects.property(Boolean)
        this.icon = project.objects.fileProperty()
    }

    @Override
    Iterable<String> asArguments() {
        List<String> cmd = []

        cmd.add("-nosign")

        if (simple.isPresent()) {
            cmd.add("-Bmac.dmg.simple=" + String.valueOf(simple.get()))
        }

        if (systemWide.isPresent()) {
            cmd.add("-BsystemWide=" + String.valueOf(systemWide.get()))
        }

        return cmd
    }

}
