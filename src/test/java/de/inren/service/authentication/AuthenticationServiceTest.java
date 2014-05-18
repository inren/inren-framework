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
