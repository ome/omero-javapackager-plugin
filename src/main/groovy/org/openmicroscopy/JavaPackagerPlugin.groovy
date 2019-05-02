/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2019 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * ------------------------------------------------------------------------------
 */
package org.openmicroscopy

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Sync
import org.gradle.internal.reflect.Instantiator
import org.gradle.jvm.tasks.Jar
import org.openmicroscopy.extensions.InstallOptions
import org.openmicroscopy.extensions.InstallOptionsContainer
import org.openmicroscopy.extensions.implementation.DefaultInstallOptions
import org.openmicroscopy.extensions.implementation.DefaultInstallOptionsContainer

import javax.inject.Inject

@CompileStatic
class JavaPackagerPlugin implements Plugin<Project> {

    public static final String MAIN_DEPLOY_NAME = "main"

    public static final String PACKAGE_APPLICATION_TASK_NAME = "packageApplication"

    public static final String DISTRIBUTION_GROUP = "distribution"

    private Project project

    private final ProjectLayout layout
    private final Instantiator instantiator
    private final CollectionCallbackActionDecorator callbackActionDecorator

    @Inject
    JavaPackagerPlugin(ProjectLayout layout, Instantiator instantiator, CollectionCallbackActionDecorator callbackActionDecorator) {
        this.layout = layout
        this.instantiator = instantiator
        this.callbackActionDecorator = callbackActionDecorator
    }

    @Override
    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(ApplicationPlugin)

        InstallOptionsContainer deployContainer =
                project.extensions.create(InstallOptionsContainer, "deploy", DefaultInstallOptionsContainer,
                        InstallOptions, instantiator, callbackActionDecorator, project)

        deployContainer.configureEach { InstallOptions options ->
            if (options instanceof DefaultInstallOptions) {
                createJavaPackagerTask(options)
            }

            if (options.name == MAIN_DEPLOY_NAME) {
                configureMain(options)
            }
        }

        deployContainer.create(MAIN_DEPLOY_NAME)
    }

    void configureMain(InstallOptions deploy) {
        // Default installer types
        final List<String> outputTypes = ["dmg", "pkg", "exe", "msi"]

        // Set the default package types
        deploy.outputTypes = outputTypes

        project.afterEvaluate {
            // Use the command line arguments from the 'run' task
            def exec = project.tasks.getByName(ApplicationPlugin.TASK_RUN_NAME) as JavaExec
            deploy.mainClassName.convention(exec.main)
            deploy.arguments.convention(exec.args)
            deploy.javaOptions.convention(exec.jvmArgs)

            // The mainJar is the archive created by the 'jar' task
            def jar = project.tasks.getByName(JavaPlugin.JAR_TASK_NAME) as Jar
            deploy.mainJar.convention(jar.archiveFileName)
            deploy.applicationVersion.convention(jar.archiveVersion)

            // Use the files from the 'installDist' task
            def installDistTask = project.tasks.getByName("installDist") as Sync
            deploy.applicationName.convention(installDistTask.destinationDir.name)
            deploy.outputFile.convention(layout.buildDirectory.file("packaged/${deploy.name}/${installDistTask.destinationDir.name}"))
            deploy.sourceDir.convention(layout.projectDirectory.dir(installDistTask.destinationDir.toString()))
            deploy.sourceFiles.from(project.fileTree(installDistTask.destinationDir).include("**/*.*"))

            outputTypes.each {
                String name = makeTaskName(deploy.name, it)
                project.tasks.named(name).configure {
                    it.dependsOn(installDistTask)
                }
            }
        }
    }

    void createJavaPackagerTask(DefaultInstallOptions deploy) {
        // Wait for evaluation as we require the values of outputTypes
        project.afterEvaluate {
            // Wait for evaluation as we require the values of outputTypes
            List<String> outputTypes = deploy.outputTypes.get()

            outputTypes.each { String outputType ->
                String taskName = makeTaskName(deploy.name, outputType)

                project.tasks.register(taskName, Exec, new Action<Exec>() {
                    @Override
                    void execute(Exec jp) {
                        jp.setGroup(DISTRIBUTION_GROUP)
                        jp.setDescription("Creates a $outputType bundle")
                        jp.commandLine("javapackager", "-deploy")
                        jp.argumentProviders.addAll(deploy.createCmdArgProviders(outputType))
                    }
                })
            }
        }
    }

    /**
     * Create a task name e.g. 'packageMatlabApplicationDmg' or 'packageMatlabApplicationExe'
     *
     * Default name when using 'main' as an install option is 'packageApplication(Dmg, Exe, etc)'
     * @param configName deploy configuration name
     * @param outputType possible output types [exe, msi, dmg, pkg]
     * @return
     */
    static String makeTaskName(String configName, String outputType) {
        String taskName = PACKAGE_APPLICATION_TASK_NAME + outputType.capitalize()
        if (configName != MAIN_DEPLOY_NAME) {
            taskName = "package" + configName.capitalize() + "Application" + outputType.capitalize()
        }
        return taskName
    }

}