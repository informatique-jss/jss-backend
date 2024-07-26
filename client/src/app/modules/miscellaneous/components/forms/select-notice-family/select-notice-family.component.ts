import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { NoticeTypeFamily } from 'src/app/modules/quotation/model/NoticeTypeFamily';
import { NoticeTypeFamilyService } from 'src/app/modules/quotation/services/notice.type.family.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-notice-family',
  templateUrl: './select-notice-family.component.html',
  styleUrls: ['./select-notice-family.component.css']
})
export class SelectNoticeFamilyComponent extends GenericSelectComponent<NoticeTypeFamily> implements OnInit {

  types: NoticeTypeFamily[] = [] as Array<NoticeTypeFamily>;

  constructor(private formBuild: UntypedFormBuilder, private noticeTypeFamilyService: NoticeTypeFamilyService,) {
    super(formBuild)
  }

  initTypes(): void {
    this.noticeTypeFamilyService.getNoticeTypeFamilies().subscribe(response => {
      this.types = response;
    })
  }
}
