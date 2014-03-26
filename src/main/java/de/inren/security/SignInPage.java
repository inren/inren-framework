package de.inren.security;

import org.apache.wicket.Application;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.string.Strings;

/**
 * @author Ingo Renner
 * 
 */
public class SignInPage extends WebPage {
	private String username;
	private String password;

	@Override
	protected void onInitialize() {
		super.onInitialize();

		StatelessForm form = new StatelessForm("form") {
			@Override
			protected void onSubmit() {
				if (Strings.isEmpty(username) || Strings.isEmpty(password))
					return;

				boolean authResult = AuthenticatedWebSession.get().signIn(
						username, password);

				if (authResult) {
					continueToOriginalDestination();
					setResponsePage(Application.get().getHomePage());
				} else {
					error("Username and password are not equal!");
				}
			}
		};

		form.setDefaultModel(new CompoundPropertyModel(this));

		form.add(new TextField("username"));
		form.add(new PasswordTextField("password"));

		form.add(new FeedbackPanel("feedbackPanel"));
		add(form);
	}
}