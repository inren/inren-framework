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
package de.inren.frontend.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.palette.Palette;
import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.inren.data.domain.security.Role;
import de.inren.data.domain.user.User;
import de.inren.frontend.common.manage.IWorktopManageDelegate;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.common.panel.IAdminPanel;
import de.inren.service.security.RoleService;
import de.inren.service.user.UserService;

/**
 * @author Ingo Renner
 */
public class EditOrCreateUserPanel extends ABasePanel implements IAdminPanel {

    @SpringBean
    private UserService userService;

    @SpringBean
    private RoleService roleService;

    private final IWorktopManageDelegate<User> delegate;

    final User user;

    private final Model<String> passwordModel = new Model<String>("");

    public EditOrCreateUserPanel(String componentId, IModel<User> m, IWorktopManageDelegate<User> delegate) {
        super(componentId);
        if (m != null) {
            user = m.getObject();
        } else {
            user = new User();
        }
        this.delegate = delegate;
    }

    @Override
    protected void initGui() {

        Form<User> form = new Form<User>("form", new CompoundPropertyModel<User>(user));

        StringResourceModel lEmail = new StringResourceModel("email.label", EditOrCreateUserPanel.this, null);
        form.add(new Label("email.label", lEmail));
        form.add(new TextField<String>("email", String.class).setRequired(true).setLabel(lEmail).add(RfcCompliantEmailAddressValidator.getInstance()));

        StringResourceModel lPwd = new StringResourceModel("password.label", EditOrCreateUserPanel.this, null);
        form.add(new Label("password.label", lPwd));
        PasswordTextField password = new PasswordTextField("password");
        password.setModel(passwordModel);
        password.setRequired(false).setLabel(lPwd);
        form.add(password);

        StringResourceModel lPwdrep = new StringResourceModel("passwordrepeat.label", EditOrCreateUserPanel.this, null);
        form.add(new Label("passwordrepeat.label", lPwdrep));
        PasswordTextField passwordrepeat = new PasswordTextField("passwordrepeat");
        passwordrepeat.setModel(password.getModel());
        passwordrepeat.setRequired(false).setLabel(lPwdrep);
        form.add(passwordrepeat);

        form.add(new EqualPasswordInputValidator(password, passwordrepeat));

        StringResourceModel lFname = new StringResourceModel("firstname.label", EditOrCreateUserPanel.this, null);
        form.add(new Label("firstname.label", lFname));
        form.add(new TextField<String>("firstname", String.class).setRequired(true).setLabel(lFname));

        StringResourceModel lLname = new StringResourceModel("lastname.label", EditOrCreateUserPanel.this, null);
        form.add(new Label("lastname.label", lLname));
        form.add(new TextField<String>("lastname", String.class).setRequired(true).setLabel(lLname));

        // TODO die Boolean Werte
        StringResourceModel lLaccountNonExpired = new StringResourceModel("accountNonExpired.label", EditOrCreateUserPanel.this, null);
        form.add(new Label("accountNonExpired.label", lLaccountNonExpired));
        form.add(new CheckBox("accountNonExpired").setLabel(lLaccountNonExpired));

        StringResourceModel laccountNonLocked = new StringResourceModel("accountNonLocked.label", EditOrCreateUserPanel.this, null);
        form.add(new Label("accountNonLocked.label", laccountNonLocked));
        form.add(new CheckBox("accountNonLocked").setLabel(laccountNonLocked));

        StringResourceModel lLcredentialsNonExpired = new StringResourceModel("credentialsNonExpired.label", EditOrCreateUserPanel.this, null);
        form.add(new Label("credentialsNonExpired.label", lLcredentialsNonExpired));
        form.add(new CheckBox("credentialsNonExpired").setLabel(lLcredentialsNonExpired));

        List<Role> allRoles = new ArrayList<Role>();
        try {
            allRoles = roleService.loadAllRoles();
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        }
        StringResourceModel lRoles = new StringResourceModel("roles.label", EditOrCreateUserPanel.this, null);
        form.add(new Label("roles.label", lRoles));

        form.add(new Palette<Role>("roles", new ListModel<Role>(allRoles), new ChoiceRenderer<Role>("name", "id"), 5, false));

        form.add(new AjaxLink<Void>("cancel") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getSession().getFeedbackMessages().clear();
                delegate.switchToComponent(target, delegate.getManagePanel());
            }
        });

        form.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    String password = passwordModel.getObject();
                    if (!"".equals(password)) {
                        ((User) form.getDefaultModelObject()).setPassword(password);
                    }
                    User u = userService.save((User) form.getModelObject());
                    form.info(new StringResourceModel("feedback.success", EditOrCreateUserPanel.this, null).getString());
                    delegate.switchToComponent(target, delegate.getManagePanel());
                } catch (RuntimeException e) {
                    form.error(new StringResourceModel("TODO", EditOrCreateUserPanel.this, null).getString());
                    target.add(getFeedback());
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                FeedbackPanel f = getFeedback();
                if (target != null && f != null) {
                    target.add(f);
                }
            }
        });

        add(form);
    }
}
