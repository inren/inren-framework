package de.inren;

import java.util.Map;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar.ComponentInitializer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import de.inren.admin.AdminOnlyPage;
import de.inren.security.BasicAuthenticationSession;
import de.inren.security.SignInPage;
import de.inren.service.Initializable;
import de.inren.service.user.UserService;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 * 
 * @see de.inren.Start#main(String[])
 */
public class WicketApplication extends AuthenticatedWebApplication {
	private final static Logger log = LoggerFactory.getLogger(WicketApplication.class);
	
	@SpringBean 
	UserService userService;
	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	@Override
	public Class<? extends WebPage> getHomePage()
	{
		return HomePage.class;
	}

	/**
	 * @see org.apache.wicket.Application#init()
	 */
	@Override
	public void init()
	{
		super.init();

		// add your configuration here
		this.getComponentInstantiationListeners().add(new SpringComponentInjector(this));
		
		getSecuritySettings().setAuthorizationStrategy(new MetaDataRoleAuthorizationStrategy(this));
		MetaDataRoleAuthorizationStrategy.authorize(AdminOnlyPage.class, Roles.ADMIN);
		
		initializeServices();
		
		mountPage("admin", AdminOnlyPage.class);
		
	}
    
	protected void initializeServices() {
        Map<String, Initializable> services = getApplicationContext().getBeansOfType( Initializable.class);
        log .info("Services = " + (services == null ? "no services found." : services.keySet()));

        if (services != null) {
            for (Initializable service : services.values()) {
                try {
                	service.init();
                } catch (Exception e) {
                    log.error("failed to initialize service", e);
                }
            }
        }
    }
    
	private ApplicationContext getApplicationContext() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
    }
	
	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		return BasicAuthenticationSession.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		return SignInPage.class;
	}
}
