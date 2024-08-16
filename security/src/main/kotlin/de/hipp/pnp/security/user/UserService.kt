package de.hipp.pnp.security.user;

import de.hipp.pnp.security.Role;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean userExists(String sub) {
        return userRepository.getUserByExternalIdentifer(sub) != null;
    }

    public String getRole(String sub) {
        String role = "ANNONYMOUS";
        User user = userRepository.getUserByExternalIdentifer(sub);
        if (user != null) {
            role = user.getRole().toString();
        }
        return role;
    }

    public User createUser(Map<String, Object> attributes) {
        return create(attributes, false);
    }

    public User createAdmin(Map<String, Object> attributes) {
        return create(attributes, true);
    }

    private User create(Map<String, Object> attributes, boolean isAdmin) {
        if (attributes.isEmpty()) {
            return null;
        }
        if (!attributes.containsKey("email_verified")) {
            return null;
        }
        if (!"true".equals(String.valueOf(attributes.get("email_verified")))) {
            return null;
        }
        User newUser = new User();
        newUser.setMail(attributes.get("email").toString());
        newUser.setNachname(attributes.get("family_name").toString());
        newUser.setVorname(attributes.get("given_name").toString());
        newUser.setName(attributes.get("name").toString());
        newUser.setExternalIdentifer(attributes.get("sub").toString());
        newUser.setRole(isAdmin ? Role.ADMIN.toString() : Role.USER.toString());
        userRepository.save(newUser);
        return newUser;
    }

    public void updateUser(User maskedUser) {
        userRepository.save(maskedUser);
    }

    public User getUserByExternalId(String externalUserId) {
        var user = userRepository.getUserByExternalIdentifer(externalUserId);
        return user;
    }
}
