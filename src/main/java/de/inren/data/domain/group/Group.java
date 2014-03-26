package de.inren.data.domain.group;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import de.inren.data.domain.role.Role;

@Entity(name="GroupOfRoles")
public class Group implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
    @Column(nullable = false, unique=true)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Group_Role", joinColumns = { @JoinColumn(name = "groupId", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "roleId", referencedColumnName = "id") })
    private Set<Role> roles;

    @Column(nullable = true)
	private String description;
	
	public Group() {
		super();
	}

	public Group(String name) {
		this(name, null);
	}

	public Group(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", roles=" + roles
				+ ", description=" + description + "]";
	}
   
}