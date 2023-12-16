import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { BatchStatistics } from '../model/BatchStatictics';

@Injectable({
  providedIn: 'root'
})
export class BatchStatisticsService extends AppRestService<BatchStatistics>{

  constructor(http: HttpClient) {
    super(http, "batch");
  }

  getBatchStatistics(): Observable<BatchStatistics[]> {
    return this.getListCached(new HttpParams(), "statistics");
  }
}
