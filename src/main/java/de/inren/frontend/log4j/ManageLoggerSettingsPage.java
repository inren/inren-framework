/**
 * 
 */
package de.inren.frontend.log4j;

import org.apache.wicket.Component;

import de.inren.frontend.common.templates.SecuredPage;

/**
 * @author Ingo Renner
 *
 */
public class ManageLoggerSettingsPage extends SecuredPage<Void> {

	@Override
	public Component createPanel(String wicketId) {
		return new ManageLoggerSettingsPanel(wicketId);
	}
}
