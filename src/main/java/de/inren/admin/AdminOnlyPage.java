package de.inren.admin;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;

/**
 * @author Ingo Renner
 * 
 */
public class AdminOnlyPage extends WebPage {
	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new Link<Void>("goToHomePage") {

			@Override
			public void onClick() {
				setResponsePage(getApplication().getHomePage());
			}
		});
	}
}