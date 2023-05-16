import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { Attachment } from '../model/Attachment';
import { AttachmentType } from '../model/AttachmentType';
import { IAttachment } from '../model/IAttachment';

@Injectable({
  providedIn: 'root'
})
export class PdfToolsService extends AppRestService<IAttachment>{

  constructor(http: HttpClient) {
    super(http, "pdf-tools");
  }

  uploadAttachment(file: File, filename: string): Observable<any> {
    let formData = new FormData();
    formData.append("file", file);
    return this.uploadPost('attachment/upload', file, formData);
  }

  downloadAttachment(attachment: Attachment) {
    this.downloadGet(new HttpParams().set("idAttachment", attachment.id + ""), "attachment/download");
  }


}
