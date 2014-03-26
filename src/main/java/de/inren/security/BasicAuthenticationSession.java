package de.inren.security;

import java.util.Collection;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;

import de.inren.data.domain.user.User;
import de.inren.service.authentication.AuthenticationService;

/**
 * @author Ingo Renner
 * 
 */
public class BasicAuthenticationSession extends AuthenticatedWebSession {
	private final static Logger log = LoggerFactory
			.getLogger(BasicAuthenticationSession.class);

	private User user;

	public User getUser() {
		return user;
	}

	 @SpringBean
	 private AuthenticationService authenticationService;

	public BasicAuthenticationSession(Request request) {
		super(request);
		Injector.get().inject(this);
	}

	@Override
	public boolean authenticate(String username, String password) {
		try {
			this.user = authenticationService.authenticateUser(username, password);
			return this.user!=null;
		} catch (Exception e) {
			log.error("authentication failed", e);
		}
		return false;
	}

	public Roles getRoles() {
		Roles resultRoles = new Roles();
		// TODO
		return resultRoles;
	}

	@Override
	public void signOut() {
		super.signOut();
		user = null;
	}
}