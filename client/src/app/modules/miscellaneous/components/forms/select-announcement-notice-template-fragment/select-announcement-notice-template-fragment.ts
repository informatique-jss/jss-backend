import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AnnouncementNoticeTemplateFragment } from 'src/app/modules/quotation/model/AnnouncementNoticeTemplateFragment';
import { AppService } from 'src/app/services/app.service';
import { AnnouncementNoticeTemplateFragmentService } from '../../../../quotation/services/announcement.notice.template.fragment.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-announcement-notice-template-fragment',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: false
})
export class SelectAnnouncementNoticeTemplateFragmentComponent extends GenericSelectComponent<AnnouncementNoticeTemplateFragment> implements OnInit {

  types: AnnouncementNoticeTemplateFragment[] = [] as Array<AnnouncementNoticeTemplateFragment>;
  constructor(private formBuild: UntypedFormBuilder, private announcementNoticeFragmentService: AnnouncementNoticeTemplateFragmentService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.announcementNoticeFragmentService.getAnnouncementNoticeTemplateFragments().subscribe(response => {
      this.types = response;
    })
  }
}
