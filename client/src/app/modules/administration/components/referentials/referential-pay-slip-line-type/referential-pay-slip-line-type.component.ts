import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { PaySlipLineType } from 'src/app/modules/accounting/model/PaySlipLineType';
import { PaySlipLineTypeService } from 'src/app/modules/accounting/services/pay.slip.line.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-pay-slip-line-type',
  templateUrl: './referential-pay-slip-line-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialPaySlipLineTypeComponent extends GenericReferentialComponent<PaySlipLineType> implements OnInit {
  constructor(private paySlipLineTypeService: PaySlipLineTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<PaySlipLineType> {
    return this.paySlipLineTypeService.addOrUpdatePaySlipLineType(this.selectedEntity!);
  }
  getGetObservable(): Observable<PaySlipLineType[]> {
    return this.paySlipLineTypeService.getPaySlipLineTypes();
  }
}
