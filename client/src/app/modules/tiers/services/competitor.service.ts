import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Competitor } from '../../tiers/model/Competitor';

@Injectable({
  providedIn: 'root'
})
export class CompetitorService extends AppRestService<Competitor>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getCompetitors() {
    return this.getList(new HttpParams(), "competitors");
  }
  
   addOrUpdateCompetitor(competitor: Competitor) {
    return this.addOrUpdate(new HttpParams(), "competitor", competitor, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
