import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { BatchStatus } from '../model/BatchStatus';

@Injectable({
  providedIn: 'root'
})
export class BatchStatusService extends AppRestService<BatchStatus>{

  constructor(http: HttpClient) {
    super(http, "batch");
  }

  getBatchStatus(): Observable<BatchStatus[]> {
    return this.getListCached(new HttpParams(), "batch-status");
  }
}
