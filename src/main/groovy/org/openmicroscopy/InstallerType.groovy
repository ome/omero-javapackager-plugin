package org.openmicroscopy

enum InstallerType {
    EXE("exe"),
    MSI("msi"),
    DMG("dmg"),
    PKG("pkg")

    private final String type

    InstallerType(String type) {
        this.type = type
    }
}