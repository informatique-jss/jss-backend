import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { formatDate } from 'src/app/libs/FormatHelper';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { BillingItem } from '../../../model/BillingItem';
import { BillingItemService } from '../../../services/billing.item.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-billing-item',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteBillingItemComponent extends GenericLocalAutocompleteComponent<BillingItem> implements OnInit {

  types: BillingItem[] = [] as Array<BillingItem>;

  constructor(private formBuild: UntypedFormBuilder, private billingItemService: BillingItemService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  filterEntities(types: BillingItem[], value: string): BillingItem[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(billingItem => this.filterBillingItem(billingItem, filterValue));
  }

  filterBillingItem(billingItem: BillingItem, filterValue: string): boolean {
    if (billingItem && billingItem.billingType) {
      if (billingItem.billingType.label && billingItem.billingType.label.toLowerCase().includes(filterValue))
        return true;
      if (billingItem.billingType.code && billingItem.billingType.code.toLowerCase().includes(filterValue))
        return true;
      if (billingItem.billingType.accountingAccountCharge && billingItem.billingType.accountingAccountCharge.accountingAccountSubNumber
        && billingItem.billingType.accountingAccountCharge.principalAccountingAccount.code
        && (billingItem.billingType.accountingAccountCharge.principalAccountingAccount.code + billingItem.billingType.accountingAccountCharge.accountingAccountSubNumber).toLowerCase().includes(filterValue))
        return true;
      if (billingItem.billingType.accountingAccountProduct && billingItem.billingType.accountingAccountProduct.accountingAccountSubNumber
        && billingItem.billingType.accountingAccountProduct.principalAccountingAccount.code
        && (billingItem.billingType.accountingAccountProduct.principalAccountingAccount.code + billingItem.billingType.accountingAccountProduct.accountingAccountSubNumber).toLowerCase().includes(filterValue))
        return true;
    }
    return false;
  }


  initTypes(): void {
    this.billingItemService.getBillingItems().subscribe(response => this.types = response);
  }

  displayLabel(object: BillingItem): string {
    return object ? object.billingType.label + " (" + object.billingType.code + " / " + formatDate(object.startDate) + ")" : '';
  }

}

