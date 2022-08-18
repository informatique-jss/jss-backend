import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { RefundType } from 'src/app/modules/tiers/model/RefundType';
import { RefundTypeService } from 'src/app/modules/tiers/services/refund.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-refund-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialRefundTypeComponent extends GenericReferentialComponent<RefundType> implements OnInit {
  constructor(private refundTypeService: RefundTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<RefundType> {
    return this.refundTypeService.addOrUpdateRefundType(this.selectedEntity!);
  }
  getGetObservable(): Observable<RefundType[]> {
    return this.refundTypeService.getRefundTypes();
  }
}
