package org.openmicroscopy.extensions

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.openmicroscopy.extensions.implementation.DefaultInstallOptions


@CompileStatic
interface InstallOptionsContainer extends NamedDomainObjectContainer<DefaultInstallOptions> {
}
