import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { FormeJuridiqueService } from 'src/app/modules/miscellaneous/services/guichet-unique/forme.juridique.service';
import { FormeJuridique } from '../../../../../quotation/model/guichet-unique/referentials/FormeJuridique';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-forme-juridique',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteFormeJuridiqueComponent extends GenericLocalAutocompleteComponent<FormeJuridique> implements OnInit {

  types: FormeJuridique[] = [] as Array<FormeJuridique>;

  constructor(private formBuild: UntypedFormBuilder, private FormeJuridiqueService: FormeJuridiqueService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: FormeJuridique[], value: string): FormeJuridique[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.FormeJuridiqueService.getFormeJuridique().subscribe(response => this.types = response);
  }

  displayLabel(object: any): string {
    if (object && object.label)
      return object.label + " (" + object.labelShort + ")";
    if (typeof object === "string")
      return object;
    return "";
  }
}
