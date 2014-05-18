/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.inren.service.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import de.inren.data.domain.user.User;
import de.inren.service.user.UserService;
import de.inren.service.user.UserServiceImpl;
import de.inren.testsupport.InRenJUnit4SpringContextTests;

/**
 * @author Ingo Renner
 * 
 */
public class UserServiceImplTest extends InRenJUnit4SpringContextTests {
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
