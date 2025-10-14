import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AnnouncementNoticeTemplate } from 'src/app/modules/quotation/model/AnnouncementNoticeTemplate';
import { AnnouncementNoticeTemplateService } from 'src/app/modules/quotation/services/announcement.notice.template.service';
import { AppService } from 'src/app/services/app.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-notice-template',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteNoticeTemplateComponent extends GenericLocalAutocompleteComponent<AnnouncementNoticeTemplate> implements OnInit {

  types: AnnouncementNoticeTemplate[] = [] as Array<AnnouncementNoticeTemplate>;

  @Input() filterAvailableEntities: AnnouncementNoticeTemplate[] | undefined;
  @Input() displayCode: boolean = false;

  constructor(private formBuild: UntypedFormBuilder, private announcementNoticeTemplateService: AnnouncementNoticeTemplateService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: AnnouncementNoticeTemplate[], value: string): AnnouncementNoticeTemplate[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(announcementNoticeTemplate =>
      announcementNoticeTemplate.label != undefined && announcementNoticeTemplate.code != undefined
      && (announcementNoticeTemplate.label.toLowerCase().includes(filterValue) || announcementNoticeTemplate.code.includes(filterValue)));
  }

  initTypes(): void {
    this.announcementNoticeTemplateService.getAnnouncementNoticeTemplates().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  displayLabel(object: any): string {
    let label = super.displayLabel(object);
    if (this.displayCode && object.code)
      label += " (" + object.code + ")";
    return label;
  }

}
