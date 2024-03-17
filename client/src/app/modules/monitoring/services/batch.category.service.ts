import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { BatchCategory } from '../model/BatchCategory';

@Injectable({
  providedIn: 'root'
})
export class BatchCategoryService extends AppRestService<BatchCategory>{

  constructor(http: HttpClient) {
    super(http, "batch");
  }

  getBatchCategories(): Observable<BatchCategory[]> {
    return this.getListCached(new HttpParams(), "batch-categories");
  }

  addOrUpdateBatchSetting(batchCategory: BatchCategory) {
    this.clearListCache(new HttpParams(), "category");
    return this.addOrUpdate(new HttpParams(), "batch-category", batchCategory);
  }
}
