import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { BillingItem } from 'src/app/modules/miscellaneous/model/BillingItem';
import { BillingItemService } from 'src/app/modules/miscellaneous/services/billing.item.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-billing-item',
  templateUrl: 'referential-billing-item.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBillingItemComponent extends GenericReferentialComponent<BillingItem> implements OnInit {
  constructor(private billingItemService: BillingItemService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<BillingItem> {
    return this.billingItemService.addOrUpdateBillingItem(this.selectedEntity!);
  }
  getGetObservable(): Observable<BillingItem[]> {
    return this.billingItemService.getBillingItems();
  }


  getElementCode(element: BillingItem) {
    return "TVA : " + (element.vat ? element.vat.rate : "") + " %";
  }

  getElementLabel(element: BillingItem) {
    return element.billingType.label;
  }
}
