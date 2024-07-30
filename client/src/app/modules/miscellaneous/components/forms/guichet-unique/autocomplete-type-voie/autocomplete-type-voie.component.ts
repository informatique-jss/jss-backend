import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeVoieService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.voie.service';
import { TypeVoie } from '../../../../../quotation/model/guichet-unique/referentials/TypeVoie';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-type-voie',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteTypeVoieComponent extends GenericLocalAutocompleteComponent<TypeVoie> implements OnInit {

  types: TypeVoie[] = [] as Array<TypeVoie>;

  constructor(private formBuild: UntypedFormBuilder, private TypeVoieService: TypeVoieService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: TypeVoie[], value: string): TypeVoie[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.TypeVoieService.getTypeVoie().subscribe(response => this.types = response);
  }

}
