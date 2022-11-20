import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Language } from '../../miscellaneous/model/Language';

@Injectable({
  providedIn: 'root'
})
export class LanguageService extends AppRestService<Language>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getLanguages() {
    return this.getListCached(new HttpParams(), "languages");
  }

  addOrUpdateLanguage(language: Language) {
    this.clearListCache(new HttpParams(), "languages");
    return this.addOrUpdate(new HttpParams(), "language", language, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
