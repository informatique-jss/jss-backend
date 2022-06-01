import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { RecordType } from '../../quotation/model/RecordType';

@Injectable({
  providedIn: 'root'
})
export class RecordTypeService extends AppRestService<RecordType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getRecordTypes() {
    return this.getList(new HttpParams(), "record-types");
  }

}
