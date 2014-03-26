package de.inren.data.repositories.role;

import org.springframework.data.repository.PagingAndSortingRepository;

import de.inren.data.domain.role.Role;

/**
 * @author Ingo Renner
 *
 */
public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {
	Role findRoleByName(String name);
}