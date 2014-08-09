/**
 * 
 */
package de.inren.frontend.log4j;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import de.inren.data.domain.loggerbean.LoggerBean;
import de.inren.frontend.common.panel.ABasePanel;
import de.inren.frontend.right.EditOrCreateRightPanel;

/**
 * @author Ingo Renner
 *
 */
public class LoggerFilterPanel extends ABasePanel {

	private IModel<LoggerBean> filterModel;

	public LoggerFilterPanel(String id, IModel<LoggerBean> model) {
		super(id);
		this.filterModel = model;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		Form<Void> form = new Form<Void>("form");
		TextField<String> tfName = new TextField<String>("name", new PropertyModel<String>(filterModel.getObject(), "name"));
		TextField<String> tfLevel = new TextField<String>("level", new PropertyModel<String>(filterModel.getObject(), "level"));
		Button submit = new Button("submit"){
			@Override
			public void onSubmit() {
				super.onSubmit();
			}
		};
		Button reset = new Button("reset"){
			@Override
			public void onSubmit() {
				filterModel.getObject().setName(null);
				filterModel.getObject().setLevel(null);
				super.onSubmit();
			}
		};
        final StringResourceModel lName = new StringResourceModel("name.label", LoggerFilterPanel.this, null);
        form.add(tfName.setLabel(lName));
        form.add(new Label("name.label", lName));

        final StringResourceModel lLevel = new StringResourceModel("level.label", LoggerFilterPanel.this, null);
        form.add(tfName.setLabel(lLevel));
        form.add(new Label("level.label", lLevel));
        form.add(tfLevel.setLabel(lLevel));
        
		form.add(submit);
		form.add(reset);
		add(form);
		
	}
}
