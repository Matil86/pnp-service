package de.hipp.pnp.genefunk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface GeneFunkGenomeRepository extends JpaRepository<GeneFunkGenome, Integer> {
	Optional<GeneFunkGenome> findByName(String name);
}
