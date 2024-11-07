package de.hipp.pnp.genefunk

import org.springframework.stereotype.Component

@Component
internal class GeneFunkClassBootstrap(private val repository: GeneFunkClassRepository) {
    init {
        initialize()
    }

    protected fun initialize() {
        if (repository.findByName("Biohacker").isEmpty) {
            repository.save(this.initiateBiohacker())
        }
        if (repository.findByName("Gunfighter").isEmpty) {
            repository.save(this.initiateGunfighter())
        }
    }


    private fun initiateBiohacker(): GeneFunkClass {
        val biohacker = GeneFunkClass()
        biohacker.setName("Biohacker")
        return biohacker
    }

    private fun initiateGunfighter(): GeneFunkClass {
        val biohacker = GeneFunkClass()
        biohacker.setName("Gunfighter")
        return biohacker
    }
}
