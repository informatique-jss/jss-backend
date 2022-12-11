import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Provider } from '../../../model/Provider';
import { ProviderService } from '../../../services/provider.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-provider',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteProviderComponent extends GenericLocalAutocompleteComponent<Provider> implements OnInit {

  types: Provider[] = [] as Array<Provider>;

  constructor(private formBuild: UntypedFormBuilder, private providerService: ProviderService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  filterEntities(types: Provider[], value: string): Provider[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(provider => provider && provider.label.toLowerCase().includes(filterValue));
  }

  initTypes(): void {
    this.providerService.getProviders().subscribe(response => this.types = response);
  }
}
