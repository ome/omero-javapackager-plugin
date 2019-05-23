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
