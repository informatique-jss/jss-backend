import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ShalNoticeTemplate } from 'src/app/modules/quotation/model/ShalNoticeTemplate';
import { ShalNoticeTemplateService } from 'src/app/modules/quotation/services/shal.notice.template.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-shal-notice-template',
  templateUrl: 'referential-shal-notice-template.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialShalNoticeTemplateComponent extends GenericReferentialComponent<ShalNoticeTemplate> implements OnInit {
  constructor(private shalNoticeTemplateService: ShalNoticeTemplateService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<ShalNoticeTemplate> {
    return this.shalNoticeTemplateService.addOrUpdateShalNoticeTemplate(this.selectedEntity!);
  }
  getGetObservable(): Observable<ShalNoticeTemplate[]> {
    return this.shalNoticeTemplateService.getShalNoticeTemplates();
  }
}
