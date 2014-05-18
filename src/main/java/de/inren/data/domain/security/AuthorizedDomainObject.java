package de.inren.data.domain.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import de.inren.data.domain.core.DomainObject;

@Entity
public abstract class AuthorizedDomainObject extends DomainObject implements AccessAuthorization {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "domain_role", joinColumns = { @JoinColumn(name = "domain_role_domainid", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "domain_role_roleid", referencedColumnName = "id") })
    private Collection<Role> roles;

    @Override
    public Collection<Role> getRoles() {
        if (roles == null) {
            this.roles = new ArrayList<Role>();
        }
        return roles;
    }

    @Override
    public void setRoles(Collection<Role> roles) {
        this.roles = new ArrayList<Role>();
        this.roles.addAll(roles);
    }

    /**
     * Return all roles from getRoles plus roles the object gets from groups.
     */
    @Override
    public Collection<Role> getGrantedRoles() {
        Collection<Role> grantedRoles = new HashSet<Role>();
        grantedRoles.addAll(getRoles());
        return grantedRoles;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AuthorizedDomainObject [roles=");
        builder.append(roles);
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }

}
