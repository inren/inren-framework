package de.inren.service.authentication;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.inren.data.domain.user.User;
import de.inren.testsupport.InRenJUnit4SpringContextTests;

/**
 * @author Ingo Renner
 *
 */
public class AuthenticationServiceTest extends InRenJUnit4SpringContextTests {

	@Autowired
	AuthenticationService authenticationService;
	
	
	@Before
    public void setUp() {
		assertNotNull(authenticationService);
        ((AuthenticationServiceImpl) authenticationService).init();
    }
	
	@Test
	public void testAuthenticateUser() {
		// Default user is admin@localhost with password geheim.
		final User user = authenticationService.authenticateUser("admin@localhost", "geheim");
		assertNotNull(user);
	}
}
