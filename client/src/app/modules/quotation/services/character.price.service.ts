import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Department } from '../../miscellaneous/model/Department';
import { CharacterPrice } from '../../quotation/model/CharacterPrice';

@Injectable({
  providedIn: 'root'
})
export class CharacterPriceService extends AppRestService<CharacterPrice>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getCharacterPrices() {
    return this.getList(new HttpParams(), "character-prices");
  }

  getCharacterPrice(department: Department, date: Date) {
    return this.get(new HttpParams().set("departmentId", department.id).set("date", date.toISOString()), "character-price");
  }

  addOrUpdateCharacterPrice(characterPrice: CharacterPrice) {
    return this.addOrUpdate(new HttpParams(), "character-price", characterPrice, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
