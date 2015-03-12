/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.inren.frontend.navigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.INavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.ImmutableNavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar.ComponentPosition;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton;
import de.inren.data.domain.security.ComponentAccess;
import de.inren.data.domain.security.Role;
import de.inren.frontend.admin.AdminPage;
import de.inren.frontend.application.HomePage;
import de.inren.frontend.auth.LoginPage;
import de.inren.frontend.blogpost.BlogPostsPage;
import de.inren.frontend.blogpost.ManageBlogPostsPage;
import de.inren.frontend.carlogbook.CarLogEntriesUserPage;
import de.inren.frontend.carlogbook.ManageCarLogEntriesPage;
import de.inren.frontend.carlogbook.ManageCarsPage;
import de.inren.frontend.componentaccess.ManageComponentAccessPage;
import de.inren.frontend.dbproperty.ManageDbPropertiesPage;
import de.inren.frontend.group.ManageGroupsPage;
import de.inren.frontend.health.BmiWikiPage;
import de.inren.frontend.health.HealthSettingsPage;
import de.inren.frontend.health.ManageMeasurementsPage;
import de.inren.frontend.health.PlotFatPage;
import de.inren.frontend.health.PlotWaterPage;
import de.inren.frontend.health.PlotWeightPage;
import de.inren.frontend.health.backup.BackupRestorePage;
import de.inren.frontend.log4j.ManageLoggerSettingsPage;
import de.inren.frontend.mailserver.ManageMailserverPage;
import de.inren.frontend.right.ManageRightsPage;
import de.inren.frontend.role.ManageRolesPage;
import de.inren.frontend.storehouse.FileManagerPage;
import de.inren.frontend.user.ManageUsersPage;
import de.inren.frontend.usersettings.UserSettingsPage;
import de.inren.service.security.ComponentAccessService;
import de.inren.service.security.RoleService.Roles;

/**
 * 
 * Decide which item appears under which navigationbar.
 * 
 * Hard coded version. Has to be replaced by a db service once in the future.
 * 
 * @author Ingo Renner
 * 
 */
public class NavigationProvider {

    @SpringBean
    private ComponentAccessService    componentAccessService;

    private static final List<String> EMPTY_LIST = Collections.<String> emptyList();

    private static NavigationProvider navigationProvider;

    private GTree<NavigationElement>  tree;

    private NavigationProvider() {
        Injector.get().inject(this);
        initNavigation();
    }

    public static NavigationProvider get() {
        if (navigationProvider == null) {
            navigationProvider = new NavigationProvider();
        }
        return navigationProvider;
    }

    private Collection<String> getRoles(String name) {
        ComponentAccess componentAccess = componentAccessService.findComponentAccessByName(name);
        Collection<String> res = new ArrayList<String>();
        if (componentAccess != null) {
            for (Role role : componentAccess.getGrantedRoles()) {
                for (SimpleGrantedAuthority simpleGrantedAuthority : role.asAuthority()) {
                    res.add(simpleGrantedAuthority.getAuthority());
                }
            }
        }
        return res;
    }

    public List<INavbarComponent> getTopNavbarComponents(Collection<String> roles, Component component) {

        List<INavbarComponent> res = new ArrayList<INavbarComponent>();
        // Root
        if (hasRight(getRoles(tree.getRoot().getData().getClazz().getSimpleName()), roles)) {
            res.add(createINavbarComponent(tree.getRoot().getData(), component));
        }
        // and 1 level
        for (GNode<NavigationElement> e : tree.getRoot().getChildren()) {
            if (hasRight(getRoles(e.getData().getClazz().getSimpleName()), roles)) {
                res.add(createINavbarComponent(e.getData(), component));
            }
        }

        return res;
    }

    public GNode<NavigationElement> getSideNavbarComponents(Class clazz, Collection<String> roles, Component component) {

        GNode<NavigationElement> res = null;
        GNode<NavigationElement> r = null;
        for (GNode<NavigationElement> n : tree.getRoot().getChildren()) {

            if (findNode(n, clazz) != null) {
                r = n;
                break;
            }
        }
        if (r != null) {
            if (hasRight(getRoles(r.getData().getClazz().getSimpleName()), roles)) {
                res = new GNode<NavigationElement>(r.getData());
            }
            // and 1 level
            for (GNode<NavigationElement> e : r.getChildren()) {
                if (hasRight(getRoles(e.getData().getClazz().getSimpleName()), roles)) {
                    res.addChild(e);
                }
            }
        }
        return res;
    }

    private GNode<NavigationElement> findNode(GNode<NavigationElement> node, Class clazz) {
        if (node.getData().getClazz().equals(clazz)) {
            return node;
        }
        for (GNode<NavigationElement> n : node.getChildren()) {
            GNode<NavigationElement> r = findNode(n, clazz);
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    private INavbarComponent createINavbarComponent(NavigationElement data, Component component) {
        return new ImmutableNavbarComponent(new NavbarButton<LoginPage>(data.getClazz(), new StringResourceModel(data.getLanguageKey(), component, null)),
                data.getPosition());
    }

    private boolean hasRight(Collection<String> needRoles, Collection<String> givenRoles) {
        if (needRoles.isEmpty()) {
            return true;
        }
        for (String r : needRoles) {
            if (r.contains(":")) {
                if (givenRoles.contains(r)) {
                    return true;
                }
            } else {
                for (String roleRight : givenRoles) {
                    String role = roleRight.split(":")[0];
                    if (r.equals(role)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void initNavigation() {

        // TODO read this data from an editable source like db or xml file.
        List<String> healthRoles = Arrays.asList(Roles.ROLE_USER.name(), Roles.ROLE_ADMIN.name());

        // static hack to speed up development for other places
        GNode<NavigationElement> root = new GNode<NavigationElement>(new NavigationElement(HomePage.class, "Home.label", EMPTY_LIST, ComponentPosition.LEFT))
                .addChild(
                        new GNode<NavigationElement>(
                                new NavigationElement(ManageMeasurementsPage.class, "Health.label", healthRoles, ComponentPosition.LEFT),
                                Arrays.asList(
                                        new GNode<NavigationElement>(new NavigationElement(PlotWeightPage.class, "Wheight.label", healthRoles,
                                                ComponentPosition.LEFT)),
                                        new GNode<NavigationElement>(new NavigationElement(PlotFatPage.class, "Fat.label", healthRoles, ComponentPosition.LEFT)),
                                        new GNode<NavigationElement>(new NavigationElement(PlotWaterPage.class, "Water.label", healthRoles,
                                                ComponentPosition.LEFT)),
                                        new GNode<NavigationElement>(new NavigationElement(BmiWikiPage.class, "BMI.label", healthRoles, ComponentPosition.LEFT)),
                                        new GNode<NavigationElement>(new NavigationElement(BackupRestorePage.class, "Backup/Restore.label", healthRoles,
                                                ComponentPosition.LEFT)),
                                        new GNode<NavigationElement>(new NavigationElement(HealthSettingsPage.class, "Settings.label", Arrays
                                                .asList(Roles.ROLE_USER.name()), ComponentPosition.LEFT)))))
                .addChild(
                        new GNode<NavigationElement>(new NavigationElement(AdminPage.class, "Admin.label", Arrays.asList(Roles.ROLE_ADMIN.name()),
                                ComponentPosition.RIGHT), Arrays.asList(
                                new GNode<NavigationElement>(new NavigationElement(ManageUsersPage.class, "Users.label",
                                        Arrays.asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageGroupsPage.class, "Groups.label", Arrays.asList(Roles.ROLE_ADMIN
                                        .name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageRightsPage.class, "Rights.label", Arrays.asList(Roles.ROLE_ADMIN
                                        .name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageComponentAccessPage.class, "ComponentAccess.label", Arrays
                                        .asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageMailserverPage.class, "MailServer.label", Arrays
                                        .asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageRolesPage.class, "Roles.label",
                                        Arrays.asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageDbPropertiesPage.class, "DbProperty.label", Arrays
                                        .asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageCarsPage.class, "Car.label", Arrays.asList(Roles.ROLE_ADMIN.name()),
                                        ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageCarLogEntriesPage.class, "CarLogEntries.label", Arrays
                                        .asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(CarLogEntriesUserPage.class, "CarLogEntriesUser.label", Arrays
                                        .asList(Roles.ROLE_USER.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageBlogPostsPage.class, "BlogPost.label", Arrays.asList(Roles.ROLE_ADMIN
                                        .name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(BlogPostsPage.class, "BlogPostView.label", Arrays.asList(Roles.ROLE_ADMIN
                                        .name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageLoggerSettingsPage.class, "Logger.label", Arrays
                                        .asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)))))
                .addChild(
                        new GNode<NavigationElement>(new NavigationElement(UserSettingsPage.class, "Settings.label", Arrays.asList(Roles.ROLE_USER.name(),
                                Roles.ROLE_ADMIN.name()), ComponentPosition.RIGHT)))
                .addChild(
                        new GNode<NavigationElement>(new NavigationElement(FileManagerPage.class, "Caretaker.label", Arrays.asList(Roles.ROLE_USER.name(),
                                Roles.ROLE_ADMIN.name()), ComponentPosition.RIGHT)));

        tree = new GTree<NavigationElement>(root);
    }

    private void initNavigationOld() {

        // TODO read this data from an editable source like db or xml file.
        List<String> healthRoles = Arrays.asList(Roles.ROLE_USER.name(), Roles.ROLE_ADMIN.name());

        // static hack to speed up development for other places
        GNode<NavigationElement> root = new GNode<NavigationElement>(new NavigationElement(HomePage.class, "Home.label", EMPTY_LIST, ComponentPosition.LEFT))
                .addChild(
                        new GNode<NavigationElement>(
                                new NavigationElement(ManageMeasurementsPage.class, "Health.label", healthRoles, ComponentPosition.LEFT),
                                Arrays.asList(
                                        new GNode<NavigationElement>(new NavigationElement(PlotWeightPage.class, "Wheight.label", healthRoles,
                                                ComponentPosition.LEFT)),
                                        new GNode<NavigationElement>(new NavigationElement(PlotFatPage.class, "Fat.label", healthRoles, ComponentPosition.LEFT)),
                                        new GNode<NavigationElement>(new NavigationElement(PlotWaterPage.class, "Water.label", healthRoles,
                                                ComponentPosition.LEFT)),
                                        new GNode<NavigationElement>(new NavigationElement(BmiWikiPage.class, "BMI.label", healthRoles, ComponentPosition.LEFT)),
                                        new GNode<NavigationElement>(new NavigationElement(BackupRestorePage.class, "Backup/Restore.label", healthRoles,
                                                ComponentPosition.LEFT)),
                                        new GNode<NavigationElement>(new NavigationElement(HealthSettingsPage.class, "Settings.label", Arrays
                                                .asList(Roles.ROLE_USER.name()), ComponentPosition.LEFT)))))
                .addChild(
                        new GNode<NavigationElement>(new NavigationElement(AdminPage.class, "Admin.label", Arrays.asList(Roles.ROLE_ADMIN.name()),
                                ComponentPosition.RIGHT), Arrays.asList(
                                new GNode<NavigationElement>(new NavigationElement(ManageUsersPage.class, "Users.label",
                                        Arrays.asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageGroupsPage.class, "Groups.label", Arrays.asList(Roles.ROLE_ADMIN
                                        .name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageRightsPage.class, "Rights.label", Arrays.asList(Roles.ROLE_ADMIN
                                        .name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageComponentAccessPage.class, "ComponentAccess.label", Arrays
                                        .asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageMailserverPage.class, "MailServer.label", Arrays
                                        .asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageRolesPage.class, "Roles.label",
                                        Arrays.asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageDbPropertiesPage.class, "DbProperty.label", Arrays
                                        .asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageBlogPostsPage.class, "BlogPost.label", Arrays.asList(Roles.ROLE_ADMIN
                                        .name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(BlogPostsPage.class, "BlogPostView.label", Arrays.asList(Roles.ROLE_ADMIN
                                        .name()), ComponentPosition.LEFT)),

                                new GNode<NavigationElement>(new NavigationElement(ManageLoggerSettingsPage.class, "Logger.label", Arrays
                                        .asList(Roles.ROLE_ADMIN.name()), ComponentPosition.LEFT)))))
                .addChild(
                        new GNode<NavigationElement>(new NavigationElement(UserSettingsPage.class, "Settings.label", Arrays.asList(Roles.ROLE_USER.name(),
                                Roles.ROLE_ADMIN.name()), ComponentPosition.RIGHT)))
                .addChild(
                        new GNode<NavigationElement>(new NavigationElement(FileManagerPage.class, "Caretaker.label", Arrays.asList(Roles.ROLE_USER.name(),
                                Roles.ROLE_ADMIN.name()), ComponentPosition.RIGHT)));

        tree = new GTree<NavigationElement>(root);
    }
}
