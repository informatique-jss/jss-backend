import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IndicatorValue } from '../model/IndicatorValue';

@Injectable({
  providedIn: 'root'
})
export class IndicatorValueService extends AppRestService<IndicatorValue> {

  constructor(http: HttpClient) {
    super(http, "indicator");
  }

  getIndicatorValues(indicatorId: number) {
    return this.getList(new HttpParams().set("idindicator", indicatorId), "indicator/values");
  }

}
