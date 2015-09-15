/**
 * 
 */
package de.inren.data.repositories.tagging;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import de.inren.data.domain.banking.Category;
import de.inren.data.domain.tagging.Tag;

/**
 * @author Ingo Renner
 *
 */
public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {

    List<Tag> findByName(String name);

}
