import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { NoticeType } from '../../../../quotation/model/NoticeType';
import { NoticeTypeFamily } from '../../../../quotation/model/NoticeTypeFamily';
import { NoticeTypeService } from '../../../../quotation/services/notice.type.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-multiple-notice-type',
  templateUrl: '../generic-select/generic-multiple-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectMultipleNoticeTypeComponent extends GenericMultipleSelectComponent<NoticeTypeFamily> implements OnInit {

  @Input() types: NoticeType[] = [] as Array<NoticeType>;
  @Input() noticeTypeFamily: NoticeTypeFamily | undefined;

  constructor(private formBuild: UntypedFormBuilder,
    private noticeTypeService: NoticeTypeService) {
    super(formBuild)
  }

  initTypes(): void {
    this.noticeTypeService.getNoticeTypes().subscribe(response => {
      if (this.noticeTypeFamily)
        this.types = response.filter(t => t.noticeTypeFamily.id == this.noticeTypeFamily!.id);
      else
        this.types = response;
    })
  }
}
