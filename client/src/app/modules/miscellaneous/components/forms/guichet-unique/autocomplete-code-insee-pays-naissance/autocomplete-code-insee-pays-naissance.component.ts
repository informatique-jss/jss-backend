import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CodeInseePaysNaissanceService } from 'src/app/modules/miscellaneous/services/guichet-unique/code.insee.pays.naissance.service';
import { CodeInseePaysNaissance } from '../../../../../quotation/model/guichet-unique/referentials/CodeInseePaysNaissance';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-code-insee-pays-naissance',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteCodeInseePaysNaissanceComponent extends GenericLocalAutocompleteComponent<CodeInseePaysNaissance> implements OnInit {

  types: CodeInseePaysNaissance[] = [] as Array<CodeInseePaysNaissance>;

  constructor(private formBuild: UntypedFormBuilder, private CodeInseePaysNaissanceService: CodeInseePaysNaissanceService,) {
    super(formBuild)
  }

  filterEntities(types: CodeInseePaysNaissance[], value: string): CodeInseePaysNaissance[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.CodeInseePaysNaissanceService.getCodeInseePaysNaissance().subscribe(response => this.types = response);
  }

}
