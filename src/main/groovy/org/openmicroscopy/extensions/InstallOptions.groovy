package org.openmicroscopy.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.openmicroscopy.Extensible
import org.openmicroscopy.extensions.implementation.MacOptions
import org.openmicroscopy.extensions.implementation.WinOptions


@CompileStatic
interface InstallOptions extends Named, Extensible {

    /**
     * Description of the application
     * @return
     */
    Provider<String> getDescription()

    /**
     * Name of the application and/or installer
     * @return
     */
    Provider<String> getApplicationName()

    /**
     * Qualified name of the application main class to execute.
     * @return
     */
    Provider<String> getMainClassName()

    /**
     * The name of the main JAR of the application.
     * containing the main class (specified as a path relative to {@code getSourceDir).
     * @return
     */
    Provider<String> getMainJar()

    /**
     * List of types of the installer to create. Valid values are: {"exe", "msi", "rpm", "deb", "pkg", "dmg"}.
     * @return
     */
    Provider<List<String>> getOutputTypes()

    /**
     * Command line arguments to pass to the main class if no command line arguments are given to the launcher
     * @return
     */
    Provider<List<String>> getArguments()

    /**
     * Options to pass to the Java runtime
     * @return
     */
    Provider<List<String>> getJavaOptions()

    /**
     * Location of the End User License Agreement (EULA) to be presented or recorded by the bundler.
     * @return RegularFile object
     */
    Provider<RegularFile> getLicenseFile()

    /**
     * File to be created by packager tooll
     * @return RegularFile object
     */
    Provider<RegularFile> getOutputFile()

    Provider<Directory> getSourceDir()

    FileCollection getSourceFiles()

    void exe(Action<? super WinOptions> action)

    void msi(Action<? super WinOptions> action)

    void dmg(Action<? super MacOptions> action)

    void pkg(Action<? super MacOptions> action)

}