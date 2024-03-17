import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { Batch } from '../model/Batch';
import { BatchSearch } from '../model/BatchSearch';
import { BatchSettings } from '../model/BatchSettings';

@Injectable({
  providedIn: 'root'
})
export class BatchService extends AppRestService<Batch>{

  constructor(http: HttpClient) {
    super(http, "batch");
  }

  getBatchs(batchSearch: BatchSearch): Observable<Batch[]> {
    return this.postList(new HttpParams(), "batch/search", batchSearch);
  }

  addOrUpdateBatchStatus(batch: Batch) {
    return this.addOrUpdate(new HttpParams(), "batch/status", batch);
  }

  declareNewBatch(batchSettings: BatchSettings, entityId: number) {
    let httpParams = new HttpParams().set("batchSettingsId", batchSettings.id);
    if (entityId)
      httpParams.set("entityId", entityId);
    return this.postItem(httpParams, "batch/new");
  }
}
