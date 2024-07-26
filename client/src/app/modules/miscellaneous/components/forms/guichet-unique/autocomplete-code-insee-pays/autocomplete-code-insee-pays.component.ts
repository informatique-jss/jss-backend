import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CodeInseePaysService } from 'src/app/modules/miscellaneous/services/guichet-unique/code.insee.pays.service';
import { CodeInseePays } from '../../../../../quotation/model/guichet-unique/referentials/CodeInseePays';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-code-insee-pays',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteCodeInseePaysComponent extends GenericLocalAutocompleteComponent<CodeInseePays> implements OnInit {

  types: CodeInseePays[] = [] as Array<CodeInseePays>;

  constructor(private formBuild: UntypedFormBuilder, private CodeInseePaysService: CodeInseePaysService,) {
    super(formBuild)
  }

  filterEntities(types: CodeInseePays[], value: string): CodeInseePays[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.CodeInseePaysService.getCodeInseePays().subscribe(response => this.types = response);
  }

}
