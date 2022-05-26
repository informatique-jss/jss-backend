import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/appRest.service';
import { AttachmentType } from '../model/AttachmentType';
import { BillingClosureRecipientType } from '../model/BillingClosureRecipientType';
import { Tiers } from '../model/Tiers';
import { TiersAttachment } from '../model/TiersAttachment';

@Injectable({
  providedIn: 'root'
})
export class UploadTiersAttachmentService extends AppRestService<Tiers>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  uploadTiersAttachment(file: File, tiers: Tiers, attachmentType: AttachmentType, filename: string): Observable<HttpEvent<any>> {
    let formData = new FormData();
    formData.append("idTiers", tiers.id + "");
    formData.append("idAttachmentType", attachmentType.id + "");
    formData.append("filename", filename);
    return this.uploadPost('tiers-attachment/upload', file, formData);
  }

  previewAttachment(tiersAttachment: TiersAttachment) {
    this.previewFileGet(new HttpParams().set("idAttachment", tiersAttachment.id + ""), "tiers-attachment/preview");
  }

  downloadAttachment(tiersAttachment: TiersAttachment) {
    this.downloadGet(new HttpParams().set("idAttachment", tiersAttachment.id + ""), "tiers-attachment/preview");
  }

}
