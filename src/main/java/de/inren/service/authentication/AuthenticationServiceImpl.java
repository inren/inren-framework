package de.inren.service.authentication;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.user.User;
import de.inren.service.Initializable;
import de.inren.service.user.UserService;

/**
 * @author Ingo Renner
 *
 */

@Service(value = "authenticationService")
@Transactional(readOnly = true)
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService, Initializable {

	private final static Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

	@Resource
	private UserService userService;

	private boolean initDone = false;

	@Override
	public void init() {
		if (!initDone) {
			log.info("AuthenticationService init start.");
			userService.init();
			initDone = true;
			log.info("AuthenticationService init done.");
		}
	}

	@Override
	public UserDetails loadUserByUsername(String ident)	throws UsernameNotFoundException {
		final User user = userService.loadByIdent(ident);
		if (user == null) {
			throw new UsernameNotFoundException("no user for ident: " + ident);
		}
		return user;
	}

	@Override
	public User authenticateUser(String ident, String password) {
		final User user = userService.authenticateUser(ident, password);
		if (user == null) {
			throw new UsernameNotFoundException("no user for ident: " + ident);
		}
		return user;
	}

}
