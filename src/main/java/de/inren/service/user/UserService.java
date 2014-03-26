package de.inren.service.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.inren.data.domain.user.User;
import de.inren.service.Initializable;

/**
 * @author Ingo Renner
 *
 */
public interface UserService extends UserDetailsService, Initializable {

	@Override
	UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

	User loadByIdent(String ident);

	User save(User user);

	User authenticateUser(String ident, String password);

}
