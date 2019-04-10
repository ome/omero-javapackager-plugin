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
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Sync
import org.gradle.internal.reflect.Instantiator
import org.gradle.jvm.tasks.Jar
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

    private final Instantiator instantiator
    private final CollectionCallbackActionDecorator callbackActionDecorator

    @Inject
    JavaPackagerPlugin(Instantiator instantiator, CollectionCallbackActionDecorator callbackActionDecorator) {
        this.instantiator = instantiator
        this.callbackActionDecorator = callbackActionDecorator
    }

    @Override
    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(ApplicationPlugin)

        InstallOptionsContainer deployContainer = project.extensions.create(InstallOptionsContainer, "deploy",
                DefaultInstallOptionsContainer, DefaultInstallOptions, instantiator, callbackActionDecorator, project)

        deployContainer.all { DefaultInstallOptions deployExt ->
            createJavaPackagerTask(deployExt)
        }

        configureForDeploy(deployContainer.create(MAIN_DEPLOY_NAME))
    }

    void configureForDeploy(DefaultInstallOptions deploy) {
        // Default installer types
        final List<String> outputTypes = ["dmg", "exe"]

        // Set the default package types
        deploy.outputTypes = outputTypes

//        deploy.exe {
//            it.addShortcut = true
//            it.installUserLevel = true
//            it.installDirChooser = true
//            it.icon = project.file("${project.projectDir}/icons/omeroinsight.ico")
//        }
        deploy.dmg {
            it.icon = project.file("${project.projectDir}/icons/omeroinsight.icns")
        }

        // Use the mainClassName and jvm args from the 'application' extension
        JavaApplication javaApplication = project.extensions.getByName("application") as JavaApplication
        deploy.mainClassName = javaApplication.mainClassName
        deploy.javaOptions = javaApplication.applicationDefaultJvmArgs

        // Use the command line arguments from the 'run' task
        JavaExec runTask = project.tasks.named(ApplicationPlugin.TASK_RUN_NAME, JavaExec).get()
        deploy.arguments = runTask.args

        // The mainJar is the archive created by the 'jar' task
        Jar jarTask = project.tasks.named(JavaPlugin.JAR_TASK_NAME, Jar).get()
        deploy.mainJar = jarTask.archiveFileName

        // Use the files from the 'installDist' task
        Sync installDistTask = project.tasks.named("installShadowDist", Sync).get()
        deploy.outputFile = project.file("${project.buildDir}/packaged/${deploy.name}/${installDistTask.destinationDir.name}")
        deploy.applicationName = installDistTask.destinationDir.name
        deploy.sourceDir = installDistTask.destinationDir
        deploy.sourceFiles.from(project.fileTree(installDistTask.destinationDir).include("**/*.*"))

        if (deploy.name == MAIN_DEPLOY_NAME) {
            project.afterEvaluate {
                outputTypes.each {
                    String name = makeTaskName(deploy.name, it)
                    project.tasks.named(name).configure {
                        it.dependsOn(installDistTask)
                    }
                }
            }
        }
    }

    void createJavaPackagerTask(DefaultInstallOptions deploy) {
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
     * @param configName
     * @param outputType
     * @return
     */
    String makeTaskName(String configName, String outputType) {
        String taskName = PACKAGE_APPLICATION_TASK_NAME + outputType.capitalize()
        if (configName != MAIN_DEPLOY_NAME) {
            taskName = "package" + configName.capitalize() + "Application" + outputType.capitalize()
        }
        return taskName
    }

}


//void configureForDeploy(DefaultInstallOptions deploy, Distribution distribution) {
//    TaskProvider<Sync> installDistTask
//    if (distribution.name == DistributionPlugin.MAIN_DISTRIBUTION_NAME) {
//        installDistTask = project.tasks.named("installDist", Sync.class)
//
//        JavaApplication javaApplication = project.extensions.getByType(JavaApplication)
//        deploy.mainClassName = javaApplication.mainClassName
//        deploy.javaOptions = javaApplication.applicationDefaultJvmArgs
//    } else {
//        installDistTask = project.tasks.named("install" + distribution.name.capitalize() + "Dist", Sync.class)
//    }
//
//    if (installDistTask != null) {
//        deploy.sourceFiles.from(project.fileTree(installDistTask.get().outputs).include("**/*.*"))
//    }
//}