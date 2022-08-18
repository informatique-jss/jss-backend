import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { BillingClosureRecipientType } from 'src/app/modules/tiers/model/BillingClosureRecipientType';
import { BillingClosureRecipientTypeService } from 'src/app/modules/tiers/services/billing.closure.recipient.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-billing-closure-recipient-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBillingClosureRecipientTypeComponent extends GenericReferentialComponent<BillingClosureRecipientType> implements OnInit {
  constructor(private billingClosureRecipientType: BillingClosureRecipientTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<BillingClosureRecipientType> {
    return this.billingClosureRecipientType.addOrUpdateBillingClosureRecipientType(this.selectedEntity!);
  }
  getGetObservable(): Observable<BillingClosureRecipientType[]> {
    return this.billingClosureRecipientType.getBillingClosureRecipientTypes();
  }
}
