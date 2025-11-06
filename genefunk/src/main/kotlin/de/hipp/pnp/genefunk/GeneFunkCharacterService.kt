package de.hipp.pnp.genefunk

import de.hipp.pnp.api.fivee.interfaces.FiveECharacterService
import de.hipp.pnp.base.entity.GeneFunkClass
import de.hipp.pnp.base.entity.Skills
import de.hipp.pnp.base.fivee.Attribute5e
import de.hipp.pnp.base.fivee.DiceRoller
import de.hipp.pnp.base.rabbitmq.UserInfoProducer
import io.github.oshai.kotlinlogging.KotlinLogging
import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.stereotype.Service
import kotlin.math.max
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

/**
 * Service for managing GeneFunk character generation and operations.
 *
 * Provides functionality for creating, retrieving, and deleting GeneFunk characters
 * with random attribute generation based on 5e rules.
 */
@Service
open class GeneFunkCharacterService(
    private val repository: GeneFunkCharacterRepository,
    private val genomeService: GeneFunkGenomeService,
    private val classService: GeneFunkClassService,
    private val userInfoProducer: UserInfoProducer,
    private val characterNamesProperties: CharacterNamesProperties,
    private val meterRegistry: MeterRegistry
) : FiveECharacterService<GeneFunkCharacter?> {
    private val random = Random.Default

    // Metrics
    private val characterGenerationCounter: Counter = Counter.builder("characters.generated.total")
        .description("Total number of characters generated")
        .register(meterRegistry)

    private val characterGenerationFailureCounter: Counter = Counter.builder("characters.generation.failures.total")
        .description("Total number of failed character generations")
        .register(meterRegistry)

    private val characterDeletionCounter: Counter = Counter.builder("characters.deleted.total")
        .description("Total number of characters deleted")
        .register(meterRegistry)

    private val characterGenerationTimer: Timer = Timer.builder("characters.generation.duration")
        .description("Time taken to generate a character")
        .register(meterRegistry)


    /**
     * Retrieves all characters for a given user.
     *
     * Admin users receive all characters, while regular users only receive their own.
     *
     * @param userId The external user identifier
     * @return List of characters accessible to the user
     */
    override fun getAllCharacters(userId: String?): MutableList<GeneFunkCharacter?>? {
        val customer = userInfoProducer.getCustomerInfoFor(userId)
        return if (customer.role.equals("admin", ignoreCase = true)) {
            repository.findAll()
        } else {
            repository.findByUserId(customer.externalIdentifer)
        }
    }

    /**
     * Generates a new character with default settings.
     *
     * @return A newly generated GeneFunk character
     */
    @Timed(value = "characters.generation.duration", description = "Time to generate character")
    override fun generate(): GeneFunkCharacter {
        return generate(GeneFunkCharacter(), "unknown")
    }

    /**
     * Generates a character with optional pre-filled data.
     *
     * Populates any null attributes with randomly generated values according to 5e rules.
     * Uses 4d6 drop lowest for attribute generation.
     *
     * @param character Optional character object with pre-filled data
     * @param externalId The external user identifier to associate with the character
     * @return A fully generated and persisted GeneFunk character
     */
    @Timed(value = "characters.generation.duration", description = "Time to generate character")
    fun generate(character: GeneFunkCharacter?, externalId: String?): GeneFunkCharacter {
        return characterGenerationTimer.recordCallable {
            try {
                logger.debug { "Generating character for user: $externalId" }

                val char = character ?: GeneFunkCharacter().apply {
                    firstName = pickRandom(characterNamesProperties.names.toMutableList())
                    lastName = pickRandom(characterNamesProperties.names.toMutableList())
                }

                val genomes = genomeService.allGenomes()
                val classes = classService.getAllClasses()

                // Set default level if not provided
                char.level = char.level ?: 1

                // Generate attributes using 4d6 drop lowest if not already set
                char.strength = char.strength ?: Attribute5e(DiceRoller.roll(4, 6, 3, true))
                char.dexterity = char.dexterity ?: Attribute5e(DiceRoller.roll(4, 6, 3, true))
                char.constitution = char.constitution ?: Attribute5e(DiceRoller.roll(4, 6, 3, true))
                char.intelligence = char.intelligence ?: Attribute5e(DiceRoller.roll(4, 6, 3, true))
                char.wisdom = char.wisdom ?: Attribute5e(DiceRoller.roll(4, 6, 3, true))
                char.charisma = char.charisma ?: Attribute5e(DiceRoller.roll(4, 6, 3, true))

                // Assign random genome if available and not set
                if (genomes.isNotEmpty() && char.genome == null) {
                    char.genome = pickRandom(genomes)
                }

                // Assign random class if available
                if (classes.isNotEmpty()) {
                    char.addClass(
                        getGenomeClass(
                            randomClassName = pickRandom(classes.keys.toMutableList()),
                            classes = classes
                        )
                    )
                }

                char.userId = externalId
                char.initialize()
                val savedCharacter = repository.saveAndFlush(char)

                // Increment success counter
                characterGenerationCounter.increment()

                logger.info {
                    "Successfully generated character: id=${savedCharacter.id}, " +
                    "name=${savedCharacter.firstName} ${savedCharacter.lastName}, " +
                    "userId=$externalId, genome=${savedCharacter.genome?.name}"
                }

                savedCharacter
            } catch (e: Exception) {
                characterGenerationFailureCounter.increment()
                logger.error(e) { "Failed to generate character for user: $externalId" }
                throw e
            }
        }!!
    }

    /**
     * Creates a GeneFunkClassEntity from a class definition.
     *
     * @param randomClassName The name of the class to create
     * @param classes Map of available class definitions
     * @return A fully configured GeneFunkClassEntity
     * @throws IllegalArgumentException if the class name is not found
     */
    private fun getGenomeClass(
        randomClassName: String,
        classes: MutableMap<String, GeneFunkClass>
    ): GeneFunkClassEntity {
        val entry = classes[randomClassName]
            ?: throw IllegalArgumentException("Class with name $randomClassName not found in classes map.")
        return GeneFunkClassEntity().apply {
            name = randomClassName
            label = entry.label
            description = entry.description
            savingThrows = entry.characterCreation.savingThrows
            startingEquipment = entry.characterCreation.startingEquipment ?: emptyList()
            skills = chooseRandomSkills(entry.characterCreation.skills)
        }
    }

    /**
     * Randomly selects skills from available options.
     *
     * @param skills The skill selection rules
     * @return List of randomly chosen skill names
     */
    private fun chooseRandomSkills(skills: Skills): List<String> {
        val skillList = skills.from.toMutableList()
        if (skills.choose > 0 && skillList.size > skills.choose) {
            val chosenSkills = mutableListOf<String>()
            repeat(skills.choose) {
                val skill = pickRandom(skillList)
                chosenSkills.add(skill)
                skillList.remove(skill)
            }
            return chosenSkills
        }
        return skillList
    }

    /**
     * Picks a random element from a list.
     *
     * @param list The list to pick from
     * @return A randomly selected element
     */
    private fun <X> pickRandom(list: MutableList<X>): X {
        val randomInt = random.nextInt(list.size)
        return list[max(randomInt, 0)]
    }

    /**
     * Deletes a character by ID.
     *
     * Admin users can delete any character, while regular users can only delete their own.
     *
     * @param characterId The ID of the character to delete
     * @param externalId The external user identifier
     * @throws IllegalArgumentException if the character doesn't exist or doesn't belong to the user
     */
    fun delete(characterId: String, externalId: String) {
        logger.debug { "Attempting to delete character: id=$characterId, userId=$externalId" }

        val customer = userInfoProducer.getCustomerInfoFor(externalId)
        val charId = characterId.toInt()

        if (customer.role.equals("admin", ignoreCase = true)) {
            repository.deleteById(charId)
            characterDeletionCounter.increment()
            logger.info { "Admin deleted character: id=$characterId, adminId=$externalId" }
            return
        }

        val character = repository.findById(charId)
        if (character.isPresent && character.get().userId == customer.externalIdentifer) {
            repository.deleteById(charId)
            characterDeletionCounter.increment()
            logger.info { "User deleted character: id=$characterId, userId=$externalId" }
        } else {
            logger.warn {
                "Unauthorized character deletion attempt: id=$characterId, userId=$externalId"
            }
            throw IllegalArgumentException("Character with ID $characterId does not exist or does not belong to user.")
        }
    }
}
