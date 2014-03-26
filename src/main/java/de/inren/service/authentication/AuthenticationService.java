package de.inren.service.authentication;

import de.inren.data.domain.user.User;

/**
 * @author Ingo Renner
 *
 */
public interface AuthenticationService {
    User authenticateUser(String email, String password);
}
