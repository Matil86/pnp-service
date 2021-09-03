import { Injectable } from '@angular/core';
import {Character} from "../shared/character";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CharacterService {
  private httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  generateCharacter() : Observable<Character>{
    return this.httpClient.get<Character>('http://localhost:8080/character/generate');
  }

  getAll() : Observable<Character[]>{
    return this.httpClient.get<Character[]>('http://localhost:8080/character');
  }
}
