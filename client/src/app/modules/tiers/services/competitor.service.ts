import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Competitor } from '../../tiers/model/Competitor';

@Injectable({
  providedIn: 'root'
})
export class CompetitorService extends AppRestService<Competitor>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getCompetitors() {
    return this.getListCached(new HttpParams(), "competitors");
  }

  addOrUpdateCompetitor(competitor: Competitor) {
    this.clearListCache(new HttpParams(), "competitors");
    return this.addOrUpdate(new HttpParams(), "competitor", competitor, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
