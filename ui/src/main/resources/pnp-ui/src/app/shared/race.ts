import {Features} from "./features";
import {RaceAttributes} from "./raceAttributes";

export class Race{
  genomeType: string;
  name: string;
  description: string;
  attributes: RaceAttributes;
  features: Features[]


  constructor(genomeType: string, name: string, description: string, attributes: RaceAttributes, features: Features[]) {
    this.genomeType = genomeType;
    this.name = name;
    this.description = description;
    this.attributes = attributes;
    this.features = features;
  }
}
