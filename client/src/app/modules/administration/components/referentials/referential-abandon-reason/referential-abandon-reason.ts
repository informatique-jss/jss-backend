import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { DocumentType } from 'src/app/modules/miscellaneous/model/DocumentType';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
import { AbandonReason } from 'src/app/modules/miscellaneous/model/AbandonReason';
import { AbandonReasonService } from '../../../../miscellaneous/services/abandon.reason.service';

@Component({
  selector: 'referential-document-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialAbandonReasonComponent extends GenericReferentialComponent<AbandonReason> implements OnInit {
  constructor(private abandonReasonService: AbandonReasonService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }
  getAddOrUpdateObservable(): Observable<AbandonReason> {
    return this.abandonReasonService.addOrUpdateAbandonReason(this.selectedEntity!);
  }
  getGetObservable(): Observable<DocumentType[]> {
    return this.abandonReasonService.getAbandonReasons();
  }
}
