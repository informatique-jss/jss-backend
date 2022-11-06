import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { FormeJuridiqueService } from 'src/app/modules/miscellaneous/services/guichet-unique/forme.juridique.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { FormeJuridique } from '../../../../../quotation/model/guichet-unique/referentials/FormeJuridique';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-forme-juridique',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteFormeJuridiqueComponent extends GenericLocalAutocompleteComponent<FormeJuridique> implements OnInit {

  types: FormeJuridique[] = [] as Array<FormeJuridique>;

  constructor(private formBuild: UntypedFormBuilder, private FormeJuridiqueService: FormeJuridiqueService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
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

}
