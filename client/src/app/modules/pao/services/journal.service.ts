import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Journal } from '../../pao/model/Journal';

@Injectable({
  providedIn: 'root'
})
export class JournalService extends AppRestService<Journal>{

  constructor(http: HttpClient) {
    super(http, "pao");
  }

  getJournals() {
    return this.getList(new HttpParams(), "journals");
  }

  getJournal(idJournal: number) {
    return this.getById("journal", idJournal);
  }

  addOrUpdateJournal(journal: Journal) {
    return this.addOrUpdate(new HttpParams(), "journal", journal, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
