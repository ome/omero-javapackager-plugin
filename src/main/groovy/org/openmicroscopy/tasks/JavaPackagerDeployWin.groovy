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
class JavaPackagerDeployWin implements CommandLineArgumentProvider {

    @Input
    @Optional
    final Property<Boolean> installDirChooser

    @Input
    @Optional
    final Property<Boolean> installUserLevel

    @Input
    @Optional
    final Property<Boolean> addShortcut

    @Input
    @Optional
    final Property<String> copyright

    @Input
    @Optional
    final Property<String> registryVendor

    @Input
    @Optional
    final Property<String> startMenuGroup

    @InputFile
    @Optional
    final RegularFileProperty icon

    JavaPackagerDeployWin(Project project) {
        this.installDirChooser = project.objects.property(Boolean)
        this.installUserLevel = project.objects.property(Boolean)
        this.addShortcut = project.objects.property(Boolean)
        this.copyright = project.objects.property(String)
        this.registryVendor = project.objects.property(String)
        this.startMenuGroup = project.objects.property(String)
        this.icon = project.objects.fileProperty()
    }

    @Override
    Iterable<String> asArguments() {
        List<String> cmd = []

        if (installDirChooser.isPresent()) {
            cmd.add("-BinstalldirChooser="+String.valueOf(installDirChooser.get()))
        }

        if (addShortcut.isPresent()) {
            cmd.add("-BshortcutHint="+String.valueOf(addShortcut.get()))
        }

        if (copyright.isPresent()) {
            cmd.add("-Bcopyright="+copyright.get())
        }

        if (startMenuGroup.isPresent()) {
            cmd.add("-Bwin.menuGroup="+startMenuGroup.get())
        }

        if (registryVendor.isPresent()) {
            cmd.add("-Bvendor="+registryVendor.get())
        }

        return cmd
    }

}
