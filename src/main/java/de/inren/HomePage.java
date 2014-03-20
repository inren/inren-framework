package de.inren;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.WebPage;

import de.inren.data.domain.entry.Entry;
import de.inren.data.domain.user.User;
import de.inren.data.repositories.entry.EntryRepository;
import de.inren.data.repositories.user.UserRepository;

public class HomePage extends WebPage {
	@SpringBean
	private UserRepository repo;
	
	@SpringBean
	private EntryRepository entryRepository;
	
	
	public HomePage(final PageParameters parameters) {
		super(parameters);

		add(new Label("version", getApplication().getFrameworkSettings().getVersion()));

		User user = new User("testname") ;
		repo.save(user);
		
		Entry entry = new Entry("Ein Text.");
		entryRepository.save(entry);
		
    }
}
