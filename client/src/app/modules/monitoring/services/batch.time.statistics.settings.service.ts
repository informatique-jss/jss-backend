import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { BatchSettings } from '../model/BatchSettings';
import { BatchTimeStatistics } from '../model/BatchTimeStatistics';

@Injectable({
  providedIn: 'root'
})
export class BatchTimeStatisticsService extends AppRestService<BatchTimeStatistics>{

  constructor(http: HttpClient) {
    super(http, "batch");
  }

  getBatchTimeStatistics(batchSettings: BatchSettings): Observable<BatchTimeStatistics[]> {
    return this.getList(new HttpParams().set("batchSettingsId", batchSettings.id), "statistics/time");
  }
}
