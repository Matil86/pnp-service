package de.hipp.pnp.security.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User?, String?> {
    @Query("select u from User u where u.externalIdentifer=:sub")
    fun getUserByExternalIdentifer(sub: String?): User?
}
