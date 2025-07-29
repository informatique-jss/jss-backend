import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { AnnouncementNoticeTemplate } from '../model/AnnouncementNoticeTemplate';
import { NoticeTemplateDescription } from '../model/NoticeTemplateDescription';

@Injectable({
  providedIn: 'root'
})
export class NoticeTemplateService extends AppRestService<AnnouncementNoticeTemplate> {

  noticeTemplateDescription: NoticeTemplateDescription;

  constructor(http: HttpClient) {
    super(http, "quotation");
    this.noticeTemplateDescription = { service: undefined, isShowNoticeTemplate: false, displayText: "" } as any as NoticeTemplateDescription;
  }

  private noticeTemplateDescriptionSubject: BehaviorSubject<NoticeTemplateDescription | undefined> = new BehaviorSubject<NoticeTemplateDescription | undefined>(undefined);
  noticeTemplateDescriptionObservable = this.noticeTemplateDescriptionSubject.asObservable();

  changeNoticeTemplateDescription(noticeTemplateDescription: NoticeTemplateDescription) {
    this.noticeTemplateDescriptionSubject.next(noticeTemplateDescription);
  }

  getNoticeTemplateDescription() {
    return this.noticeTemplateDescription;
  }
}
