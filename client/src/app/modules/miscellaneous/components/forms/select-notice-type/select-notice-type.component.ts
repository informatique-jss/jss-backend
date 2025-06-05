import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { NoticeType } from 'src/app/modules/quotation/model/NoticeType';
import { NoticeTypeFamily } from 'src/app/modules/quotation/model/NoticeTypeFamily';
import { AppService } from 'src/app/services/app.service';
import { NoticeTypeService } from '../../../../quotation/services/notice.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-notice-type',
  templateUrl: './select-notice-type.component.html',
  styleUrls: ['./select-notice-type.component.css']
})
export class SelectNoticeTypeComponent extends GenericSelectComponent<NoticeType> implements OnInit {

  types: NoticeType[] = [] as Array<NoticeType>;

  @Input() filteredNoticeTypeFamily: NoticeTypeFamily | undefined;

  constructor(private formBuild: UntypedFormBuilder, private noticeTypeService: NoticeTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.noticeTypeService.getNoticeTypes().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label));
    })
  }
}
