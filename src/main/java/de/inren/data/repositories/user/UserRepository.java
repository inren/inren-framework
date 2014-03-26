package de.inren.data.repositories.user;

import org.springframework.data.repository.PagingAndSortingRepository;

import de.inren.data.domain.user.User;

/**
 * @author Ingo Renner
 *
 */
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
	
	User findByUsername(String username);

}
