package org.openmicroscopy.extensions.implementation

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.internal.reflect.Instantiator
import org.openmicroscopy.extensions.InstallOptionsContainer

@CompileStatic
class DefaultInstallOptionsContainer extends AbstractNamedDomainObjectContainer<DefaultInstallOptions>
        implements InstallOptionsContainer {

    private final Project project

    DefaultInstallOptionsContainer(Class<DefaultInstallOptions> type, Instantiator instantiator,
                                   CollectionCallbackActionDecorator callbackDecorator, Project project) {
        super(type, instantiator, callbackDecorator)
        this.project = project
    }

    @Override
    protected DefaultInstallOptions doCreate(String name) {
        return getInstantiator().newInstance(DefaultInstallOptions, name, project)
    }

}
