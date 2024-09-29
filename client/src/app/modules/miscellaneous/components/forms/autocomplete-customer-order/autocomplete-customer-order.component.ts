import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { IndexEntity } from '../../../../../routing/search/IndexEntity';
import { IndexEntityService } from '../../../../../routing/search/index.entity.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-customer-order',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteCustomerOrderComponent extends GenericAutocompleteComponent<IndexEntity, IndexEntity> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private indexEntityService: IndexEntityService, private appService3: AppService) {
    super(formBuild, appService3)
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

  getPreviewActionLinkFunction(entity: IndexEntity): string[] | undefined {
    return ['/order', entity.entityId + ""];
  }
}
