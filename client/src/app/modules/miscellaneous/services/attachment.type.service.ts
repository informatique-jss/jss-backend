import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AttachmentType } from '../../miscellaneous/model/AttachmentType';

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

  addOrUpdateAttachmentType(attachmentType: AttachmentType) {
    return this.addOrUpdate(new HttpParams(), "attachment-type", attachmentType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
