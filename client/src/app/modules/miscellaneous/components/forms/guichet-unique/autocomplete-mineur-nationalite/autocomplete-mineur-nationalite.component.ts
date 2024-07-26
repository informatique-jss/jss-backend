import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MineurNationaliteService } from 'src/app/modules/miscellaneous/services/guichet-unique/mineur.nationalite.service';
import { MineurNationalite } from '../../../../../quotation/model/guichet-unique/referentials/MineurNationalite';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-mineur-nationalite',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteMineurNationaliteComponent extends GenericLocalAutocompleteComponent<MineurNationalite> implements OnInit {

  types: MineurNationalite[] = [] as Array<MineurNationalite>;

  constructor(private formBuild: UntypedFormBuilder, private MineurNationaliteService: MineurNationaliteService,) {
    super(formBuild,)
  }

  filterEntities(types: MineurNationalite[], value: string): MineurNationalite[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.MineurNationaliteService.getMineurNationalite().subscribe(response => this.types = response);
  }

}
