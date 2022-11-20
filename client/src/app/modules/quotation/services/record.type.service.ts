import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { RecordType } from '../../quotation/model/RecordType';

@Injectable({
  providedIn: 'root'
})
export class RecordTypeService extends AppRestService<RecordType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getRecordTypes() {
    return this.getListCached(new HttpParams(), "record-types");
  }

  addOrUpdateRecordType(recordType: RecordType) {
    this.clearListCache(new HttpParams(), "record-types");
    return this.addOrUpdate(new HttpParams(), "record-type", recordType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
