package de.inren.data.domain.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import de.inren.data.domain.group.Group;
import de.inren.data.domain.role.Role;
/**
 * 
 * Just some user entity.
 * 
 * @author Ingo Renner
 * 
 */
@Entity
public class User implements UserDetails, Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "User_Role", joinColumns = { @JoinColumn(name = "userId", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "roleId", referencedColumnName = "id") })
    private Set<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "User_Group", joinColumns = { @JoinColumn(name = "userId", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "groupId", referencedColumnName = "id") })
    private Set<Group> groups;
    
	private String password;
	
	private String username;

	private boolean accountNonExpired;

	private boolean accountNonLocked;

	private boolean credentialsNonExpired;

	private boolean enabled;



	public User() {
	}
	
	public User(String username) {
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities;
		authorities = new HashSet<GrantedAuthority>();
		for (Role role : getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		for (Group group : getGroups()) {
			for (Role role : group.getRoles()) {
				authorities.add(new SimpleGrantedAuthority(role.getName()));
			}
		}
		return authorities;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<Role> getRoles() {
		if(roles==null) {
			this.roles = new HashSet<Role>();
		}
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Group> getGroups() {
		if(groups==null) {
			this.groups = new HashSet<Group>();
		}
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	@Override
	public String toString() {
		return "User [id=" + id 
				+ ", password=XXXXX" + ", username=" + username
				+ ", accountNonExpired=" + accountNonExpired
				+ ", accountNonLocked=" + accountNonLocked
				+ ", credentialsNonExpired=" + credentialsNonExpired
				+ ", enabled=" + enabled + "]";
	}

}
