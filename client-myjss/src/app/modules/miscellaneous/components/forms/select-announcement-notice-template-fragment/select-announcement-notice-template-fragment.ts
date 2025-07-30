import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { AnnouncementNoticeTemplateFragment } from '../../../../quotation/model/AnnouncementNoticeTemplateFragment';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-announcement-notice-template-fragment',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectAnnouncementNoticeTemplateFragmentComponent extends GenericSelectComponent<AnnouncementNoticeTemplateFragment> implements OnInit {

  @Input() types: AnnouncementNoticeTemplateFragment[] = [] as Array<AnnouncementNoticeTemplateFragment>;

  constructor(private formBuild: UntypedFormBuilder) {
    super(formBuild)
  }

  override initTypes(): void {
    this.types = this.types;
  }
}
