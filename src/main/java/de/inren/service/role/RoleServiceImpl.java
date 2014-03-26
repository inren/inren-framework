package de.inren.service.role;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.role.Role;
import de.inren.data.repositories.role.RoleRepository;
import de.inren.service.user.UserServiceImpl;

/**
 * @author Ingo Renner
 *
 */
@Service(value = "roleService")
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
	
	private final static Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

	@Resource
	RoleRepository roleRepository;

	private boolean initDone = false;

	@Override
	public void init() {
		if (!initDone) {
			log.info("RoleService init start.");
			List<Role> roles = (List<Role>) roleRepository.findAll();
			if (roles.isEmpty()) {
				roleRepository.save(new Role("admin", "Admin."));
				roleRepository.save(new Role("authenticated",
						"User is logged in."));
				roleRepository.save(new Role("all", "Anybody."));
			}
			initDone = true;
			log.info("RoleService init done.");
		}
	}

	@Override
	public Role findRoleByName(String name) {
		return roleRepository.findRoleByName(name);
	}

}
