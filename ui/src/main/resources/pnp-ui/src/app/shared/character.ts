import {Attribute} from "./attribute";
import {Race} from "./race";
import {CharacterClass} from "./class";

export class Character{
  id: number;
  firstName: string;
  lastName: string;
  level: number;
  strength: Attribute;
  dexterity:  Attribute;
  constitution:  Attribute;
  intelligence:  Attribute;
  wisdom:  Attribute;
  charisma:  Attribute;
  race: Race;
  characterClasses: CharacterClass[];


  constructor(id: number, firstName: string, lastName: string, level: number, strength: Attribute, dexterity: Attribute, constitution: Attribute, intelligence: Attribute, wisdom: Attribute, charisma: Attribute, race: Race, characterClasses: CharacterClass[]) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.level = level;
    this.strength = strength;
    this.dexterity = dexterity;
    this.constitution = constitution;
    this.intelligence = intelligence;
    this.wisdom = wisdom;
    this.charisma = charisma;
    this.race = race;
    this.characterClasses = characterClasses;
  }
}
