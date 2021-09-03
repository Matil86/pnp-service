import { Component, OnInit } from '@angular/core';
import {MenuItem} from 'primeng/api';
import {CharacterService} from "../services/character.service";

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {
  items: MenuItem[];
  private characterService: CharacterService;

  constructor(characterService:CharacterService) {
    this.characterService = characterService;
    this.items =[];
  }

  ngOnInit(): void {

    this.items = [
      {
        label: 'New',
        command: (event) => {
          console.log(event)
        }
      },
      {
        label: 'Generate',
        command: (event) => {
          this.characterService.generateCharacter()
            .subscribe(character => {
              console.log(character);
            })
        }
      },
      {
        label: 'All',
        command: (event) => {
          this.characterService.getAll()
            .subscribe(character => {
              console.log(character);
            })
        }
      }
    ];
  }
}
