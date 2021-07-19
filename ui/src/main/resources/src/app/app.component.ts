import {Component} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'charactercreator';

  constructor() {
    this.loadCharacters();
  }

  async loadCharacters() {
    const url: string = 'http://localhost:8080/character';
    const response = await fetch(url, {
      method: 'GET',
      headers: {'Content-Type': 'application/json; charset=UTF-8'}
    })
    console.log(response);
  }

}


