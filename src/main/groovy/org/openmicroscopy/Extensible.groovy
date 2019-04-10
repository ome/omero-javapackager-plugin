package org.openmicroscopy

import groovy.transform.CompileStatic
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer

@CompileStatic
trait Extensible {
    ExtensionContainer getExtensionContainer() {
        (this as ExtensionAware).extensions
    }
}