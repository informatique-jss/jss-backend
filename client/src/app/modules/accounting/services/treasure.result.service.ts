import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { TreasureResult } from '../model/TreasureResult';

@Injectable({
  providedIn: 'root'
})
export class TreasureResultService extends AppRestService<TreasureResult> {

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getTreasure() {
    return this.getList(new HttpParams(), "treasure");
  }

}
