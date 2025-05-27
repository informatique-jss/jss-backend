import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { NoticeTypeFamily } from '../../../../quotation/model/NoticeTypeFamily';
import { NoticeTypeFamilyService } from '../../../../quotation/services/notice.type.family.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-notice-type-family',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectNoticeTypeFamilyComponent extends GenericSelectComponent<NoticeTypeFamily> implements OnInit {

  @Input() types: NoticeTypeFamily[] = [] as Array<NoticeTypeFamily>;

  constructor(private formBuild: UntypedFormBuilder,
    private noticeTypeFamilyService: NoticeTypeFamilyService) {
    super(formBuild)
  }

  initTypes(): void {
    this.noticeTypeFamilyService.getNoticeTypeFamilies().subscribe(response => {
      this.types = response;
    })
  }
}
