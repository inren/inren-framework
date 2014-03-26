package de.inren.data.repositories.group;

import org.springframework.data.repository.PagingAndSortingRepository;

import de.inren.data.domain.group.Group;

/**
 * @author Ingo Renner
 *
 */
public interface GroupRepository  extends PagingAndSortingRepository<Group, Long> {
	Group findGroupByName(String Name);
}