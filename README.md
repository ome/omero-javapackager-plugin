[![Actions Status](https://github.com/ome/omero-javapackager-plugin/workflows/Gradle/badge.svg)](https://github.com/ome/omero-javapackager-plugin/actions)

## OMERO javapackager Gradle plugin

The _omero-javapackager-plugin_ is a [Gradle](https://gradle.org) plugin that provides
infrastructure for packaging Java applications for different platforms, including
``.dmg`` for Mac OS X and ``.exe`` for Windows.

### Usage

Include the following at the top of your _build.gradle_ file:

```groovy
plugins {
    id "org.openmicroscopy.packager" version "x.y.z"
}
```

Targets of the form:

```groovy

packageNAMEApplicationTYPE

```

exist for the various components. For example, to create an OMERO.importer .dmg run:

```groovy

packageImporterApplicationDmg

```

Gradle plugin configuration example:

```gradle
deploy {
    main {
        icon = "${projectDir}/icons/omeroInsight" // Change the icon, file extension is automatic depending on OS
        arguments = ["container.xml"]
    }
}
```

Full custom example:

```gradle
deploy {
    someApp {
        outputTypes = ["exe", "dmg"]                            // this deploy config will create tasks for exe or dmg installers, depending on OS
        mainClassName = "org.myurl.Main"                        // main class package and name
        javaOptions = ["-Xms256m", "-Xmx1024m"]                 // custom JVM options
        mainJar = "someApp"                                     // you application jar filename
        sourceDir = file("$buildDir/install/someApp)            // the root directory of your distributable application
        sourceFiles = fileTree("$buildDir/install/someApp).include("**/*.*") 
        applicationVersion = "$version"
        dmg {
            systemWide = true
            simple = false
        }
        msi {
            copyright = "MIT"
            addShortcut = false
        }
    }
}
```