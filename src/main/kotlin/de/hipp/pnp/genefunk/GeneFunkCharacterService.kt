package de.hipp.pnp.genefunk

import de.hipp.pnp.api.fivee.interfaces.FiveECharacterService
import de.hipp.pnp.base.entity.GeneFunkClass
import de.hipp.pnp.base.entity.Skills
import de.hipp.pnp.base.fivee.Attribute5e
import de.hipp.pnp.base.fivee.DiceRoller
import de.hipp.pnp.security.user.UserInfoProducer
import org.springframework.stereotype.Service
import java.util.Objects
import java.util.Random
import kotlin.math.max

@Service
class GeneFunkCharacterService(
    private val repository: GeneFunkCharacterRepository,
    private val genomeService: GeneFunkGenomeService,
    private val classService: GeneFunkClassService, private val userInfoProducer: UserInfoProducer
) : FiveECharacterService<GeneFunkCharacter?> {
    private val random = Random()

    private val names = mutableListOf<String>(
        "Aaden", "Aarav", "Aaren", "Aaron", "Abbie", "Abby", "Abdiel", "Abdullah", "Abel", "Abigail",
        "Abraham", "Abram", "Ace", "Adam", "Adan", "Addison", "Aden", "Aditya", "Adonis", "Adrian",
        "Adriel", "Adrien", "Agustin", "Ahmad", "Ahmed", "Aidan", "Aiden", "Aidyn", "Aimee", "Alan",
        "Albert", "Alberto", "Alec", "Alejandro", "Alessandro", "Alex", "Alexander", "Alexis", "Alexzander", "Alfie",
        "Alfonso", "Alfred", "Alfredo", "Ali", "Alijah", "Allan", "Allen", "Alonso", "Alonzo", "Alvaro",
        "Alvin", "Amari", "Ameer", "Amir", "Amos", "Amy", "Anders", "Anderson", "Andre", "Andrew",
        "Andrews", "Andy", "Angel", "Angus", "Anna", "Anthony", "Antoine", "Antonio", "Archie", "Ari",
        "Ariana", "Arian", "Arianna", "Ariel", "Arjun", "Arlo", "Armando", "Armani", "Arnav", "Aron",
        "Arthur", "Arturo", "Aryan", "Asa", "Ash", "Asher", "Ashley", "Ashton", "Aubrey", "Aubree",
        "Audrey", "August", "Augustine", "Augustus", "Austin", "Austyn", "Ava", "Avery", "Axel", "Axton",
        "Ayana", "Ayaan", "Ayden", "Aydin", "Azaria", "Bailey", "Barbara", "Beatrice", "Bev", "Bella",
        "Ben", "Benjamin", "Bennett", "Bennie", "Benny", "Bethany", "Betsy", "Betty", "Beverly", "Billie",
        "Billy", "Blair", "Blake", "Bo", "Bob", "Bobby", "Bradley", "Brandon", "Brayden", "Bret",
        "Brett", "Brian", "Brice", "Bridget", "Brooke", "Brook", "Brooklyn", "Bryce", "Brynn", "Caden",
        "Caitlin", "Cameron", "Carmen", "Carol", "Casey", "Catherine", "Charlie", "Charlotte", "Chloe", "Chris",
        "Christopher", "Claire", "Clem", "Courtney", "Cory", "Daniel", "Danny", "Danni", "David", "Dawn",
        "Demi", "Denny", "Dexter", "Diana", "Dominic", "Drew", "Dylan", "Eden", "Edward", "Eleanor",
        "Elena", "Eliana", "Eli", "Elias", "Elijah", "Elisa", "Elise", "Elizabeth", "Ella", "Ellie",
        "Elliot", "Emerson", "Emilia", "Emily", "Emma", "Erin", "Esme", "Ethan", "Eva", "Eve",
        "Evelyn", "Evie", "Faith", "Finlay", "Finley", "Fiona", "Francis", "Frankie", "Fred", "Freddie",
        "Frederick", "Freya", "Gabby", "Gabriel", "Gabe", "Gail", "George", "Georgia", "Gerald", "Gideon",
        "Grace", "Gracie", "Haiden", "Hailey", "Hannah", "Harley", "Harper", "Harrison", "Harry", "Harvey",
        "Hayden", "Hazel", "Heidi", "Henry", "Hope", "Imogen", "Isabel", "Isabella", "Isabelle", "Isaac",
        "Isla", "Ismael", "Jack", "Jackson", "Jacob", "Jade", "Jaden", "Jaime", "Jake", "James",
        "Jamie", "Jane", "Jasmine", "Jason", "Jasper", "Jay", "Jayden", "Jenna", "Jennifer", "Jessica",
        "Joe", "Joel", "John", "Jonathan", "Jordan", "Joseph", "Josh", "Joshua", "Josie", "Jude",
        "Julia", "Julian", "Julie", "Justin", "Kai", "Kaitlyn", "Kayla", "Kaylee", "Kelsey", "Kendall",
        "Kian", "Kieran", "Kim", "Kimberly", "Kris", "Kristina", "Kyle", "Lacey", "Lara", "Laura",
        "Lauren", "Layla", "Leah", "Leo", "Leon", "Lewis", "Liam", "Libby", "Lily", "Lola",
        "Louis", "Lucas", "Lucia", "Lucy", "Luna", "Luke", "Lyla", "Maddison", "Madeline", "Madelyn",
        "Madison", "Mae", "Maria", "Martha", "Martin", "Mary", "Matilda", "Matthew", "Megan", "Melissa",
        "Mia", "Michael", "Michelle", "Molly", "Morgan", "Naomi", "Natalie", "Nathan", "Nicholas", "Nicole",
        "Noah", "Oliver", "Olivia", "Oscar", "Owen", "Paige", "Patrick", "Peter", "Phoebe", "Rachel",
        "Rebecca", "Reese", "Reuben", "Rhys", "Riley", "Robert", "Rose", "Rosie", "Ruby", "Ryan",
        "Sam", "Samantha", "Samuel", "Sara", "Sarah", "Scarlett", "Scott", "Sean", "Sebastian", "Sienna",
        "Skye", "Sofia", "Sophia", "Sophie", "Spencer", "Stanley", "Summer", "Taylor", "Theo", "Thomas",
        "Toby", "Tom", "Tommy", "Tyler", "Victoria", "William", "Willow", "Zac", "Zachary", "Zak", "Zoe", "Zoey"
    )


    override fun getAllCharacters(userId: String?): MutableList<GeneFunkCharacter?>? {
        val customer = userInfoProducer.getCustomerInfoFor(userId)
        if (customer.role.equals("admin", ignoreCase = true)) {
            return repository.findAll()
        }
        return repository.findByUserId(customer.externalIdentifer)
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
            character.firstName = this.pickRandom(this.names)
            character.lastName = this.pickRandom(this.names)
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
            character.genome = pickRandom(genomes)
        }
        if (!classes.isEmpty()) {
            character.addClass(
                getGenomeClass(
                    randomClassName = pickRandom(classes.keys.toMutableList()),
                    classes = classes
                )
            )
        }
        character.userId = externalId
        character.initialize()
        return repository.saveAndFlush<GeneFunkCharacter>(character)
    }

    private fun getGenomeClass(
        randomClassName: String,
        classes: MutableMap<String, GeneFunkClass>
    ): GeneFunkClassEntity {
        val entry: GeneFunkClass = classes[randomClassName]
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

    private fun chooseRandomSkills(skills: Skills): List<String> {
        val skillList = skills.from.toMutableList()
        if (skills.choose > 0 && skillList.size > skills.choose) {
            val chosenSkills = mutableListOf<String>()
            for (i in 0 until skills.choose) {
                chosenSkills.add(pickRandom(skillList))
                skillList.remove(chosenSkills.last())
            }
            return chosenSkills
        }
        return skillList
    }

    private fun <X> pickRandom(list: MutableList<X>): X {
        val randomInt = random.nextInt(list.size)
        return list[max(randomInt, 0)]
    }

    fun delete(characterId: String, externalId: String) {
        val customer = userInfoProducer.getCustomerInfoFor(externalId)
        if ("admin".equals(customer.role, ignoreCase = true)) {
            repository.deleteById(characterId.toInt())
            return
        }
        val character = repository.findById(characterId.toInt())
        if (character.isPresent && character.get().userId == customer.externalIdentifer) {
            repository.deleteById(characterId.toInt())
        } else {
            throw IllegalArgumentException("Character with ID $characterId does not exist or does not belong to user.")
        }
    }
}
