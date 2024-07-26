import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { IndexEntity } from '../../../../../routing/search/IndexEntity';
import { IndexEntityService } from '../../../../../routing/search/index.entity.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-customer-order',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteCustomerOrderComponent extends GenericAutocompleteComponent<IndexEntity, IndexEntity> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private indexEntityService: IndexEntityService,) {
    super(formBuild)
  }

  searchEntities(value: string): Observable<IndexEntity[]> {
    return this.indexEntityService.getCustomerOrdersByKeyword(value);
  }

  displayLabel(customerOrder: IndexEntity): string {
    if (customerOrder && customerOrder.entityId)
      return customerOrder.entityId + "";
    if (customerOrder && (customerOrder as any).id)
      return (customerOrder as any).id + "";
    return "";
  }
}
