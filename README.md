### OMERO javapackager Gradle plugin

The _omero-javapackager-plugin_ is a gradle plugin that provides infrastructure to package applications.
This allows to create ``.dmg``, ``.exe``, etc.

### Usage

Include the following to the top of your _build.gradle_ file:

```groovy
plugins {
    id "org.openmicroscopy.packager" version "1.0.0"
}
```

To create OMERO.importer dmg for example, run:

```groovy

packageNameApplicationDmg

```
where ``Name`` is ``Importer``.
