import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { Language } from '../model/Language';

@Injectable({
  providedIn: 'root'
})
export class LanguageService extends AppRestService<Language> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getLanguages() {
    return this.getListCached(new HttpParams(), "languages");
  }
}
