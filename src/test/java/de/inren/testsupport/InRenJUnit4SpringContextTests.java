package de.inren.testsupport;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Default Spring context for unit tests. 
 * 
 * @author Ingo Renner
 *
 */
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/applicationTestContext.xml" })
public class InRenJUnit4SpringContextTests extends AbstractJUnit4SpringContextTests {
	
}
