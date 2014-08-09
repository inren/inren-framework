/**
 * 
 */
package de.inren.frontend.common.panel;

import org.apache.wicket.markup.html.form.ListChoice;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Ingo Renner
 *
 */
public class ListChoiceMarkupContainer extends Panel {

	public static final String CHOICES_ID = "choices";
	
	
	public ListChoiceMarkupContainer(String id, ListChoice<String> listChoice) {
		super(id);
		add(listChoice);
	}	
	
}
