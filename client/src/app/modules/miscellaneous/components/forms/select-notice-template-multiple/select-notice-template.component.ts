import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AnnouncementNoticeTemplate } from 'src/app/modules/quotation/model/AnnouncementNoticeTemplate';
import { AnnouncementNoticeTemplateService } from 'src/app/modules/quotation/services/announcement.notice.template.service';
import { AppService } from 'src/app/services/app.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-notice-template-multiple',
  templateUrl: './../generic-select/generic-multiple-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html'],
})
export class SelectNoticeTemplateMultipleComponent extends GenericMultipleSelectComponent<AnnouncementNoticeTemplate> implements OnInit {
  types: AnnouncementNoticeTemplate[] = [] as Array<AnnouncementNoticeTemplate>;

  constructor(private formBuild: UntypedFormBuilder, private noticeTemplateService: AnnouncementNoticeTemplateService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.noticeTemplateService.getAnnouncementNoticeTemplates().subscribe(response => {
      this.types = response.sort((a, b) => a.code.localeCompare(b.code));
    })
  }

  displayLabel(object: any): string {
    let label = super.displayLabel(object);
    if (object.code)
      label = object.code + " - " + label;
    return label;
  }
}
