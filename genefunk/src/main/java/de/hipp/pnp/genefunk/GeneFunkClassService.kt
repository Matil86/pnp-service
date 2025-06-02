package de.hipp.pnp.genefunk

import org.springframework.stereotype.Service

@Service
class GeneFunkClassService(private val repository: GeneFunkClassRepository) {
    fun getAllClasses(): MutableList<GeneFunkClass?> = repository.findAll()
}
