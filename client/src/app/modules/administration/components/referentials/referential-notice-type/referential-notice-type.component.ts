import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { NoticeType } from 'src/app/modules/quotation/model/NoticeType';
import { NoticeTypeService } from 'src/app/modules/quotation/services/notice.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-notice-type',
  templateUrl: 'referential-notice-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialNoticeTypeComponent extends GenericReferentialComponent<NoticeType> implements OnInit {
  constructor(private noticeTypeService: NoticeTypeService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<NoticeType> {
    return this.noticeTypeService.addOrUpdateNoticeType(this.selectedEntity!);
  }
  getGetObservable(): Observable<NoticeType[]> {
    return this.noticeTypeService.getNoticeTypes();
  }
}
