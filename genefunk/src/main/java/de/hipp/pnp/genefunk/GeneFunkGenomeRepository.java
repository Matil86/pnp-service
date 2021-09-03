package de.hipp.pnp.genefunk;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface GeneFunkGenomeRepository extends JpaRepository<GeneFunkGenome, Integer> {
    Optional<GeneFunkGenome> findByName(String name);
}
