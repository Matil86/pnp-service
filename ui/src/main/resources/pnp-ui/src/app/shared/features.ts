export class Features{
  label: string;
  description: string;
  availableAtLevel: number;


  constructor(label: string, description: string, availableAtLevel: number) {
    this.label = label;
    this.description = description;
    this.availableAtLevel = availableAtLevel;
  }
}
