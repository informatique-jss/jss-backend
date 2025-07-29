import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AnnouncementNoticeTemplate } from 'src/app/modules/quotation/model/AnnouncementNoticeTemplate';
import { ProvisionFamilyType } from 'src/app/modules/quotation/model/ProvisionFamilyType';
import { AnnouncementNoticeTemplateService } from 'src/app/modules/quotation/services/announcement.notice.template.service';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-notice-template',
  templateUrl: './select-notice-template.component.html',
  styleUrls: ['./select-notice-template.component.css']
})
export class SelectNoticeTemplateComponent extends GenericSelectComponent<AnnouncementNoticeTemplate> implements OnInit {
  types: AnnouncementNoticeTemplate[] = [] as Array<AnnouncementNoticeTemplate>;

  @Input() filteredProvisionFamilyType: ProvisionFamilyType | undefined;
  @Input() displayCode: boolean = false;

  constructor(private formBuild: UntypedFormBuilder, private noticeTemplateService: AnnouncementNoticeTemplateService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.noticeTemplateService.getAnnouncementNoticeTemplates().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  hasMatchingProvisionFamily(type: any): boolean {
    if (!this.filteredProvisionFamilyType || !type.provisionFamilyTypes) return true;
    return type.provisionFamilyTypes.some((t: any) => this.filteredProvisionFamilyType && t.id === this.filteredProvisionFamilyType.id);
  }

  displayLabel(object: any): string {
    let label = super.displayLabel(object);
    if (this.displayCode && object.code)
      label += " (" + object.code + ")";
    return label;
  }
}
