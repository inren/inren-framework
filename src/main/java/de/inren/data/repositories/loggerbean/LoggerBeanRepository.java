/**
 * 
 */
package de.inren.data.repositories.loggerbean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import de.inren.data.domain.loggerbean.LoggerBean;

/**
 * @author Ingo Renner
 *
 */
public interface LoggerBeanRepository extends
		PagingAndSortingRepository<LoggerBean, Long> {

	LoggerBean findByName(String name);

	Page<LoggerBean> findByNameLike(String name, Pageable pageable);
	
	Page<LoggerBean> findByNameLikeAndLevelLike(String name, String level, Pageable pageable);

}
