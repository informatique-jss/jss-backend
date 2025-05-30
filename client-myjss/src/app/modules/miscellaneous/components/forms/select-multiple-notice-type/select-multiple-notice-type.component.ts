import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { NoticeType } from '../../../../quotation/model/NoticeType';
import { NoticeTypeFamily } from '../../../../quotation/model/NoticeTypeFamily';
import { NoticeTypeService } from '../../../../quotation/services/notice.type.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-multiple-notice-type',
  templateUrl: '../generic-select/generic-multiple-select.component.html',
  styleUrls: ['../generic-select/generic-multiple-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectMultipleNoticeTypeComponent extends GenericMultipleSelectComponent<NoticeTypeFamily> implements OnInit {

  @Input() types: NoticeType[] = [] as Array<NoticeType>;
  @Input() noticeTypeFamily: NoticeTypeFamily | undefined;
  allTypes: NoticeType[] = [];

  constructor(private formBuild: UntypedFormBuilder,
    private noticeTypeService: NoticeTypeService) {
    super(formBuild)
  }

  initTypes(): void {
    this.noticeTypeService.getNoticeTypes().subscribe(response => {
      this.allTypes = response;
    })
  }

  setTypes() {
    if (this.noticeTypeFamily)
      this.types = this.allTypes.filter(t => t.noticeTypeFamily.id == this.noticeTypeFamily!.id);
    else
      this.types = this.allTypes;
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    if (changes['noticeTypeFamily'] && this.form != undefined) {
      this.setTypes();
    }
  }
}
