import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { BatchSettings } from '../model/BatchSettings';

@Injectable({
  providedIn: 'root'
})
export class BatchSettingsService extends AppRestService<BatchSettings>{

  constructor(http: HttpClient) {
    super(http, "batch");
  }

  getBatchSettings(): Observable<BatchSettings[]> {
    return this.getListCached(new HttpParams(), "settings");
  }

  addOrUpdateBatchSetting(batchSettings: BatchSettings) {
    this.clearListCache(new HttpParams(), "settings");
    return this.addOrUpdate(new HttpParams(), "settings", batchSettings);
  }
}
