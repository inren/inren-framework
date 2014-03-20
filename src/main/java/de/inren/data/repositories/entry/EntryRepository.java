package de.inren.data.repositories.entry;

import org.springframework.data.repository.PagingAndSortingRepository;

import de.inren.data.domain.entry.Entry;

/**
 * @author Ingo Renner
 *
 */
public interface EntryRepository extends PagingAndSortingRepository<Entry, Long> {

}
