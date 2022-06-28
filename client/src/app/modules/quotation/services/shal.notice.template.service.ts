import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { ShalNoticeTemplate } from '../../quotation/model/ShalNoticeTemplate';

@Injectable({
  providedIn: 'root'
})
export class ShalNoticeTemplateService extends AppRestService<ShalNoticeTemplate>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getShalNoticeTemplates() {
    return this.getList(new HttpParams(), "shal-notice-templates");
  }
  
   addOrUpdateShalNoticeTemplate(shalNoticeTemplate: ShalNoticeTemplate) {
    return this.addOrUpdate(new HttpParams(), "shal-notice-template", shalNoticeTemplate, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
