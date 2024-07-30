import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CodePaysService } from 'src/app/modules/miscellaneous/services/guichet-unique/code.pays.service';
import { CodePays } from '../../../../../quotation/model/guichet-unique/referentials/CodePays';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-code-pays',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteCodePaysComponent extends GenericLocalAutocompleteComponent<CodePays> implements OnInit {

  types: CodePays[] = [] as Array<CodePays>;

  constructor(private formBuild: UntypedFormBuilder, private CodePaysService: CodePaysService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: CodePays[], value: string): CodePays[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.CodePaysService.getCodePays().subscribe(response => this.types = response);
  }

}
