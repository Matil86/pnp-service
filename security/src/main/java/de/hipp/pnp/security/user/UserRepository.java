package de.hipp.pnp.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
interface UserRepository extends JpaRepository<User, String> {
    @Query("select u from User u where u.externalIdentifer=:sub")
    User getUserByExternalIdentifer(String sub);
}
