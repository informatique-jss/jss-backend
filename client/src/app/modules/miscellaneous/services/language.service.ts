import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Language } from '../model/Language';

@Injectable({
  providedIn: 'root'
})
export class LanguageService extends AppRestService<Language>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getLanguages() {
    return this.getList(new HttpParams(), "languages");
  }

}
