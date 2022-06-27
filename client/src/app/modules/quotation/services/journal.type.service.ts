import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { JournalType } from '../../quotation/model/JournalType';

@Injectable({
  providedIn: 'root'
})
export class JournalTypeService extends AppRestService<JournalType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getJournalTypes() {
    return this.getList(new HttpParams(), "journal-types");
  }

  addOrUpdateJournalType(journalType: JournalType) {
    return this.addOrUpdate(new HttpParams(), "journal-type", journalType, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
