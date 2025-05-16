import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { NoticeType } from '../../../../quotation/model/NoticeType';
import { NoticeTypeFamily } from '../../../../quotation/model/NoticeTypeFamily';
import { NoticeTypeService } from '../../../../quotation/services/notice.type.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-multiple-notice-type',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: false
})
export class SelectMultipleNoticeTypeComponent extends GenericMultipleSelectComponent<NoticeTypeFamily> implements OnInit {

  @Input() types: NoticeType[] = [] as Array<NoticeType>;

  constructor(private formBuild: UntypedFormBuilder,
    private noticeTypeService: NoticeTypeService) {
    super(formBuild)
  }

  initTypes(): void {
    this.noticeTypeService.getNoticeTypes().subscribe(response => {
      this.types = response;
    })
  }
}
