import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { IndexEntityService } from '../../../../../routing/search/index.entity.service';
import { IndexEntity } from '../../../../../routing/search/IndexEntity';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-customer-order',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteCustomerOrderComponent extends GenericAutocompleteComponent<IndexEntity, IndexEntity> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private indexEntityService: IndexEntityService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<IndexEntity[]> {
    return this.indexEntityService.getCustomerOrdersByKeyword(value);
  }

  displayLabel(customerOrder: IndexEntity): string {
    if (customerOrder)
      return customerOrder.entityId + "";
    return "";
  }
}
