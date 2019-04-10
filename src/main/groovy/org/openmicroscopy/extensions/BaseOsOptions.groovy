package org.openmicroscopy.extensions

import groovy.transform.CompileStatic
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.process.CommandLineArgumentProvider

@CompileStatic
interface BaseOsOptions {

    CommandLineArgumentProvider createCmdArgsProvider()

    Provider<RegularFile> getIcon()

}
