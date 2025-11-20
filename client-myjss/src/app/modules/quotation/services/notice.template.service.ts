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

  noticeTemplateDescription: NoticeTemplateDescription | undefined;

  constructor(http: HttpClient) {
    super(http, "quotation");
    this.noticeTemplateDescription = { service: undefined, isShowNoticeTemplate: false, displayText: "", isUsingTemplate: false, assoAffaireOrder: undefined } as any as NoticeTemplateDescription;
    this.changeNoticeTemplateDescription(this.noticeTemplateDescription);
  }

  private noticeTemplateDescriptionSubject: BehaviorSubject<NoticeTemplateDescription | undefined> = new BehaviorSubject<NoticeTemplateDescription | undefined>(undefined);
  noticeTemplateDescriptionObservable = this.noticeTemplateDescriptionSubject.asObservable();

  changeNoticeTemplateDescription(noticeTemplateDescription: NoticeTemplateDescription) {
    this.noticeTemplateDescription = noticeTemplateDescription;
    this.noticeTemplateDescriptionSubject.next(noticeTemplateDescription);
  }

  getNoticeTemplateDescription() {
    return this.noticeTemplateDescription;
  }

  clearNoticeTemplateDescription() {
    this.noticeTemplateDescription = undefined;
    this.noticeTemplateDescriptionSubject.next(this.noticeTemplateDescription);
  }
}
