import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DeviseCapitalService } from 'src/app/modules/miscellaneous/services/guichet-unique/devise.capital.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { DeviseCapital } from '../../../../../quotation/model/guichet-unique/referentials/DeviseCapital';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-devise-capital',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteDeviseCapitalComponent extends GenericLocalAutocompleteComponent<DeviseCapital> implements OnInit {

  types: DeviseCapital[] = [] as Array<DeviseCapital>;

  constructor(private formBuild: UntypedFormBuilder, private DeviseCapitalService: DeviseCapitalService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  filterEntities(types: DeviseCapital[], value: string): DeviseCapital[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.DeviseCapitalService.getDeviseCapital().subscribe(response => this.types = response);
  }

}
