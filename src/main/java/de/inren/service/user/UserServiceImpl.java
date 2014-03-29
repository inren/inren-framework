package de.inren.service.user;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.user.User;
import de.inren.data.repositories.user.UserRepository;
import de.inren.service.Initializable;
import de.inren.service.group.GroupService;
import de.inren.service.role.RoleService;

/**
 * @author Ingo Renner
 *
 */
@Service(value = "userService")
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, Initializable {
	
	private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
    @Autowired
	UserRepository userRepository;

    @Autowired
	RoleService roleService;

	@Resource
	GroupService groupService;

    @Autowired
    private PasswordEncoder passwordEncoder;

	private boolean initDone = false;

    @Override
	public void init() {
		if (!initDone) {
			log.info("UserService init start.");
			roleService.init();
			groupService.init();
			List<User> users = (List<User>) userRepository.findAll();

			if (users.isEmpty()) {
				log.info("UserService creating admin.");
				final User user = new User();
				user.setUsername("admin@localhost");
				user.setPassword("geheim");
				user.setAccountNonExpired(true);
				user.setAccountNonLocked(true);
				user.setCredentialsNonExpired(true);
				user.setEnabled(true);
				user.getRoles().add(roleService.findRoleByName("admin"));
				user.getRoles().add(
						roleService.findRoleByName("authenticated"));
				user.getRoles().add(roleService.findRoleByName("all"));
				save(user);
			}
			initDone = true;
			log.info("UserService init done.");
		}
	}

    @Override
	public User loadByIdent(String ident) {
		return (User) loadUserByUsername(ident);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Could not find any user by username = [" + username + "] .");
		}
		return user;
	}

	@Override
	@Transactional
    public User save(User user) {
        try {
            if (user.getId()==null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                User u2 = userRepository.save(user);
                log.info("created new user: {}", u2);                
                return u2; 
            } else {
                final User u = userRepository.findOne(user.getId());
                if (user.getPassword() == null || "".equals(user.getPassword())
                        || user.getPassword().equals(u.getPassword())) {
                    user.setPassword(u.getPassword());
                    log.info("updating user: {}", user);
                } else {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    log.info("updating user with new password: {}", user);
                }
                return userRepository.save(user);
            }
        } catch (Exception e) {
            log.error("error saving user: " + user, e);
            throw new RuntimeException(e);
        }
    }

	@Override
	public User authenticateUser(String ident, String password) {
		final User user = userRepository.findByUsername(ident);
		if(passwordEncoder.matches(password, user.getPassword())) {
			return user;
		}
		return null;
	}
}
