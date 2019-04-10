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
