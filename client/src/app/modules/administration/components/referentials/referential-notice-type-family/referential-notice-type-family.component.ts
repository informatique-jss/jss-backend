import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { NoticeTypeFamily } from 'src/app/modules/quotation/model/NoticeTypeFamily';
import { NoticeTypeFamilyService } from 'src/app/modules/quotation/services/notice.type.family.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-notice-type-family',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialNoticeTypeFamilyComponent extends GenericReferentialComponent<NoticeTypeFamily> implements OnInit {
  constructor(private noticeTypeFamilyService: NoticeTypeFamilyService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<NoticeTypeFamily> {
    return this.noticeTypeFamilyService.addOrUpdateNoticeTypeFamily(this.selectedEntity!);
  }
  getGetObservable(): Observable<NoticeTypeFamily[]> {
    return this.noticeTypeFamilyService.getNoticeTypeFamilies();
  }
}
