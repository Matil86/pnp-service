package de.hipp.pnp.genefunk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface GeneFunkClassRepository extends JpaRepository<GeneFunkClass, Integer> {
	GeneFunkClass findByName(String name);
}
