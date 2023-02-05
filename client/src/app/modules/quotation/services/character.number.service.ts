import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Provision } from '../model/Provision';

@Injectable({
  providedIn: 'root'
})
export class CharacterNumberService extends AppRestService<number>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getCharacterNumber(provision: Provision) {
    return this.postItem(new HttpParams(), "character/number", provision);
  }
}
