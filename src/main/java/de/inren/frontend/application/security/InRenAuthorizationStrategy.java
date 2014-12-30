/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.inren.frontend.application.security;

import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.inren.data.domain.security.ComponentAccess;
import de.inren.data.domain.security.Right;
import de.inren.data.domain.security.Role;
import de.inren.data.domain.user.User;
import de.inren.frontend.common.templates.SecuredPage;
import de.inren.security.BasicAuthenticationSession;
import de.inren.service.security.ComponentAccessService;

/**
 * @author Ingo Renner
 *
 */
public class InRenAuthorizationStrategy implements IAuthorizationStrategy {
    private final static Logger    log = LoggerFactory.getLogger(InRenAuthorizationStrategy.class);

    @SpringBean
    private ComponentAccessService componentAccessService;

    public InRenAuthorizationStrategy() {
        /* Spring injection. */
        SpringComponentInjector.get().inject(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.authorization.IAuthorizationStrategy#
     * isInstantiationAuthorized(java.lang.Class)
     */
    @Override
    public <T extends IRequestableComponent> boolean isInstantiationAuthorized(Class<T> componentClass) {
        if (!(SecuredPage.class.isAssignableFrom(componentClass))) {
            return true;
        }
        log.info("isInstantiationAuthorized : " + componentClass.getSimpleName());
        for (ComponentAccess componentAccess : componentAccessService.getComponentAccessList()) {
            log.info("Checking " + componentClass.getSimpleName() + " against componentAccess=" + componentAccess);

            if (componentClass.getSimpleName().equalsIgnoreCase(componentAccess.getName())) {
                log.info("Found componentAccess=" + componentAccess);
                BasicAuthenticationSession s = ((BasicAuthenticationSession) Session.get());
                User u = s.getUser();
                log.info("User u=" + u);
                if (u != null) {
                    log.info("user rights are: " + u.getGrantedRoles());
                    Role role = hasRole(u.getGrantedRoles(), componentAccess.getGrantedRoles());
                    log.info("role check result = " + role);
                    return role != null;
                }
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.wicket.authorization.IAuthorizationStrategy#isActionAuthorized
     * (org.apache.wicket.Component, org.apache.wicket.authorization.Action)
     */
    @Override
    public boolean isActionAuthorized(Component component, Action action) {

        if (!(SecuredPage.class.isAssignableFrom(component.getClass()))) {
            return true;
        }
        log.info("isActionAuthorized : " + component.getPath() + " action:" + action.getName());
        for (ComponentAccess componentAccess : componentAccessService.getComponentAccessList()) {

            if (component.getClass().getSimpleName().equalsIgnoreCase(componentAccess.getName())) {
                log.info("Found componentAccess=" + componentAccess);
                BasicAuthenticationSession s = ((BasicAuthenticationSession) Session.get());
                User u = s.getUser();
                log.info("User u=" + u);
                if (u != null) {
                    log.info("user rights are: " + u.getGrantedRoles());
                    Role role = hasRole(u.getGrantedRoles(), componentAccess.getGrantedRoles());
                    log.info("role check result = " + role);

                    if (isActionAllowed(action, role)) {
                        return true;
                    }
                }
            }
        }
        // TODO nur Test
        return false;
    }

    private boolean isActionAllowed(Action action, Role role) {
        for (Right right : role.getRights()) {
            if (right.getName().equalsIgnoreCase(action.getName())) {
                return true;
            }
        }
        return false;
    }

    private Role hasRole(Collection<Role> rolesAvailable, Collection<Role> rolesNeeded) {
        for (Role needed : rolesNeeded) {
            for (Role avail : rolesAvailable) {
                log.info("Role check: " + needed.getName() + " == " + avail.getName());
                if (needed.getName().equals(avail.getName())) {
                    return avail;
                }
            }
        }
        return null;
    }
}
