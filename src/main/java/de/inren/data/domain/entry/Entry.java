package de.inren.data.domain.entry;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 
 * Just some kind of data entry to the database.
 * @author Ingo Renner
 *
 */


@Entity
public class Entry implements Serializable {
 
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String entry;

	public Entry(String entry) {
		this.entry = entry;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public Entry() {
	}

	@Override
	public String toString() {
		return "Entry [id=" + id + ", entry=" + entry + "]";
	}
}
