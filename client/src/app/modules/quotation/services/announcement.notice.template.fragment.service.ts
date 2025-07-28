import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AnnouncementNoticeTemplateFragment } from '../../quotation/model/AnnouncementNoticeTemplateFragment';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementNoticeTemplateFragmentService extends AppRestService<AnnouncementNoticeTemplateFragment>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAnnouncementNoticeTemplateFragments() {
    return this.getList(new HttpParams(), "announcement-notice-template-fragment");
  }
  
   addOrUpdateAnnouncementNoticeTemplateFragment(announcementNoticeTemplateFragment: AnnouncementNoticeTemplateFragment) {
    return this.addOrUpdate(new HttpParams(), "announcement-notice-template-fragment", announcementNoticeTemplateFragment, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
