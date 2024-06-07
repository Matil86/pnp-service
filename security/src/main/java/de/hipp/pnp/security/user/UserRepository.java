package de.hipp.pnp.security.user;

import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<User, String> {
    User getUserByExternalIdentifer(String sub);
}
