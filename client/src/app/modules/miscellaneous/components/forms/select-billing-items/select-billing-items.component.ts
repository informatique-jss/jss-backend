import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BillingItem } from '../../../model/BillingItem';
import { BillingItemService } from '../../../services/billing.item.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-billing-items',
  templateUrl: './select-billing-items.component.html',
  styleUrls: ['./select-billing-items.component.css']
})
export class SelectBillingItemsComponent extends GenericSelectComponent<BillingItem> implements OnInit {

  types: BillingItem[] = [] as Array<BillingItem>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder,
    private billingItemService: BillingItemService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.billingItemService.getBillingItems().subscribe(response => {
      this.types = response;
      if (this.types)
        for (let type of this.types)
          type.startDate = new Date(type.startDate);
    })
  }
}
