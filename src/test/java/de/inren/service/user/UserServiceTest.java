package de.inren.service.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import de.inren.data.domain.user.User;
import de.inren.testsupport.InRenJUnit4SpringContextTests;

/**
 * @author Ingo Renner
 *
 */
public class UserServiceTest extends InRenJUnit4SpringContextTests {

	@Autowired
	UserService userService;
	
	
	@Before
    public void setUp() {
		assertNotNull(userService);
        ((UserServiceImpl) userService).init();
    }
	
	@Test
	public void testLoadUserByUsername() {
		// Username is the email 
		UserDetails user = userService.loadUserByUsername("admin@localhost");
		assertNotNull(user);
		// Unknown user
		try {
			userService.loadUserByUsername("unknown@localhost");
		} catch (Exception e) {
			// expected
		}
	}

	@Test
	public void testLoadByIdent() {
		// ident is the email 
		User user = userService.loadByIdent("admin@localhost");
		assertNotNull(user);
		
		// Unknown user
		try {
			userService.loadByIdent("unknown@localhost");
		} catch (Exception e) {
			// expected
		}
	}
	
	@Test
	public void testSave() {
		User user = userService.loadByIdent("admin@localhost");
		final String pwdOld = user.getPassword();
		user.setPassword("tollesPwd");
		User userSaved = userService.save(user);
		assertTrue(!"tollesPwd".equals(userSaved.getPassword()));
		assertTrue(!pwdOld.equals(userSaved.getPassword()));
		User userNewPwd = userService.authenticateUser("admin@localhost", "tollesPwd");
		assertNotNull(userNewPwd);
		// cleanup 
		userNewPwd.setPassword("geheim");
		userService.save(userNewPwd);
		User userRestored = userService.authenticateUser("admin@localhost", "geheim");
		assertNotNull(userRestored);

	}
	
	@Test
	public void testAuthenticateUser() {
		// Default user is admin@localhost with password geheim.
		final User user = userService.authenticateUser("admin@localhost", "geheim");
		assertNotNull(user);

	}

}
