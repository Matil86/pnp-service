package de.hipp.pnp.genefunk;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface GeneFunkCharacterRepository extends JpaRepository<GeneFunkCharacter, Integer> {
    List<GeneFunkCharacter> findByUserId(String userId);
}
