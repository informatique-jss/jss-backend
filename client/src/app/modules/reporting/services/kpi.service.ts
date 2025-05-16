import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Kpi } from '../../reporting/model/Kpi';

@Injectable({
  providedIn: 'root'
})
export class KpiService extends AppRestService<Kpi>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getKpis() {
    return this.getList(new HttpParams(), "kpis");
  }
  
   addOrUpdateKpi(kpi: Kpi) {
    return this.addOrUpdate(new HttpParams(), "kpi", kpi, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
