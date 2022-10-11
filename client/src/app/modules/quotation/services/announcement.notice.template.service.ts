import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AnnouncementNoticeTemplate } from '../model/AnnouncementNoticeTemplate';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementNoticeTemplateService extends AppRestService<AnnouncementNoticeTemplate>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAnnouncementNoticeTemplates() {
    return this.getList(new HttpParams(), "announcement-notice-templates");
  }

  addOrUpdateAnnouncementNoticeTemplate(announcementNoticeTemplate: AnnouncementNoticeTemplate) {
    return this.addOrUpdate(new HttpParams(), "announcement-notice-template", announcementNoticeTemplate, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
