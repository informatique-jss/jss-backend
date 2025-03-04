import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { Provider } from '../../../model/Provider';
import { ProviderService } from '../../../services/provider.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-provider',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css'],
})
export class AutocompleteProviderComponent extends GenericAutocompleteComponent<Provider, Provider> implements OnInit {

  types: Provider[] = [] as Array<Provider>;

  constructor(private formBuild: UntypedFormBuilder, private providerService: ProviderService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  searchEntities(value: string): Observable<Provider[]> {
    return this.providerService.getProviderByValue(value);
  }

  mapResponse(response: Provider[]): Provider[] {
    return response;
  }

  getPreviewActionLinkFunction(entity: Provider): string[] | undefined {
    return ['/administration/provider', entity.id + ""];
  }
}
