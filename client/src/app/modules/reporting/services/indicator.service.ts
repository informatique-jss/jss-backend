import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Indicator } from '../../reporting/model/Indicator';

@Injectable({
  providedIn: 'root'
})
export class IndicatorService extends AppRestService<Indicator>{

  constructor(http: HttpClient) {
    super(http, "indicator");
  }

  getIndicators() {
    return this.getList(new HttpParams(), "indicators");
  }
  
   addOrUpdateIndicator(indicator: Indicator) {
    return this.addOrUpdate(new HttpParams(), "indicator", indicator, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
