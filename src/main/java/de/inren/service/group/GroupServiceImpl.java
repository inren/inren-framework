package de.inren.service.group;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.group.Group;
import de.inren.data.domain.role.Role;
import de.inren.data.repositories.group.GroupRepository;
import de.inren.data.repositories.role.RoleRepository;
import de.inren.service.Initializable;
import de.inren.service.role.RoleService;
import de.inren.service.role.RoleServiceImpl;

/**
 * 
 * @author Ingo Renner
 *
 */
@Service(value = "groupService")
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {
	
	private final static Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

	private boolean initDone = false;

	@Resource
	RoleService roleService;

	@Resource
	GroupRepository groupRepository;

	@Resource
	RoleRepository roleRepository;

	@Override
	public void init() {
		if (!initDone) {
			log.info("GroupService init start.");
			roleService.init();
			List<Group> groups = (List<Group>) groupRepository.findAll();
			if (groups.isEmpty()) {
				final Group loggedInGrp = new Group("authenticated", "User is logged in.");
				Set<Role> groupRoles = new HashSet<Role>();
				groupRoles.add(roleRepository.findRoleByName("authenticated"));
				groupRoles.add(roleRepository.findRoleByName("all"));
				loggedInGrp.setRoles(groupRoles);
				groupRepository.save(loggedInGrp);
			}
			initDone = true;
			log.info("GroupService init done.");
		}

	}

}
