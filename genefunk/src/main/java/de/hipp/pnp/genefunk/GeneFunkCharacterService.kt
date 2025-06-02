package de.hipp.pnp.genefunk

import de.hipp.pnp.api.fivee.interfaces.FiveECharacterService
import de.hipp.pnp.base.fivee.Attribute5e
import de.hipp.pnp.base.fivee.DiceRoller
import de.hipp.pnp.base.rabbitmq.UserInfoProducer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Objects
import java.util.Random
import kotlin.math.max

@Service
@Transactional
open class GeneFunkCharacterService(
    private val repository: GeneFunkCharacterRepository,
    private val genomeService: GeneFunkGenomeService,
    private val classService: GeneFunkClassService, private val userInfoProducer: UserInfoProducer
) : FiveECharacterService<GeneFunkCharacter?> {
    private val random = Random()

    override fun getAllCharacters(userId: String?): MutableList<GeneFunkCharacter?>? {
        val customer = userInfoProducer.getCustomerInfoFor(userId)
        if (customer != null && customer.getRole().equals("admin", ignoreCase = true)) {
            return repository.findAll()
        }
        return repository.findByUserId(customer.getExternalIdentifer())
    }

    override fun generate(): GeneFunkCharacter {
        return this.generate(GeneFunkCharacter(), "unknown")
    }

    fun generate(character: GeneFunkCharacter?, externalId: String?): GeneFunkCharacter {
        var character = character
        val genomes = genomeService.allGenomes()
        val classes = classService.getAllClasses()

        if (Objects.isNull(character)) {
            character = GeneFunkCharacter()
        }
        if (Objects.isNull(character!!.level)) {
            character.level = 1
        }
        if (Objects.isNull(character.strength)) {
            character.strength = Attribute5e(DiceRoller.roll(4, 6, 3, true))
        }
        if (Objects.isNull(character.dexterity)) {
            character.dexterity = Attribute5e(DiceRoller.roll(4, 6, 3, true))
        }
        if (Objects.isNull(character.constitution)) {
            character.constitution = Attribute5e(DiceRoller.roll(4, 6, 3, true))
        }
        if (Objects.isNull(character.intelligence)) {
            character.intelligence = Attribute5e(DiceRoller.roll(4, 6, 3, true))
        }
        if (Objects.isNull(character.wisdom)) {
            character.wisdom = Attribute5e(DiceRoller.roll(4, 6, 3, true))
        }
        if (Objects.isNull(character.charisma)) {
            character.charisma = Attribute5e(DiceRoller.roll(4, 6, 3, true))
        }
        if (!genomes.isEmpty() && Objects.isNull(character.genome)) {
            character.genome = pickRandom<GeneFunkGenome?>(genomes) as GeneFunkGenome?
        }
        if (!classes.isEmpty() && Objects.isNull(character.characterClasses)) {
            character.addClass(pickRandom<GeneFunkClass?>(classes) as GeneFunkClass?)
        }
        character.userId = externalId
        character.initialize()
        return repository.save<GeneFunkCharacter>(character)
    }

    private fun <X> pickRandom(list: MutableList<X?>): Any? {
        val randomInt = random.nextInt(list.size)
        return list.get(max(randomInt, 0))
    }
}
