import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { AttachmentType } from '../model/AttachmentType';

@Injectable({
  providedIn: 'root'
})
export class AttachmentTypeService extends AppRestService<AttachmentType>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getAttachmentTypes() {
    return this.getList(new HttpParams(), "attachment-types");
  }

}
