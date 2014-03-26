package de.inren.service.role;

import de.inren.data.domain.role.Role;
import de.inren.service.Initializable;

/**
 * @author Ingo Renner
 *
 */
public interface RoleService extends Initializable {
	
	Role findRoleByName(String name);
	
}
