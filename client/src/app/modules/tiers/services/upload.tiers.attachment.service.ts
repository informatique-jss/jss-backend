import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/appRest.service';
import { TiersComponent } from '../components/tiers/tiers.component';
import { AttachmentType } from '../model/AttachmentType';
import { BillingClosureRecipientType } from '../model/BillingClosureRecipientType';
import { ITiers } from '../model/ITiers';
import { TiersAttachment } from '../model/TiersAttachment';

@Injectable({
  providedIn: 'root'
})
export class UploadTiersAttachmentService extends AppRestService<ITiers>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  uploadTiersAttachment(file: File, tiers: ITiers, attachmentType: AttachmentType, filename: string): Observable<HttpEvent<any>> {
    let formData = new FormData();
    if (TiersComponent.instanceOfResponsable(tiers))
      formData.append("idResponsable", tiers.id + "");
    if (TiersComponent.instanceOfTiers(tiers))
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

  findAttachmentWithSameFilename(tiers: ITiers, filename: string) {
    if (TiersComponent.instanceOfResponsable(tiers))
      return this.getList(new HttpParams().set("idResponsable", tiers.id).set("filename", filename), "tiers-attachment/search");
    return this.getList(new HttpParams().set("idTiers", tiers.id).set("filename", filename), "tiers-attachment/search");
  }

}
