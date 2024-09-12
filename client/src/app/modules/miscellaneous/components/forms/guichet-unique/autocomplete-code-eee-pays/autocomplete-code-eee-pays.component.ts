import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CodeEEEPaysService } from 'src/app/modules/miscellaneous/services/guichet-unique/code.eee.pays.service';
import { CodeEEEPays } from '../../../../../quotation/model/guichet-unique/referentials/CodeEEEPays';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-code-eee-pays',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteCodeEEEPaysComponent extends GenericLocalAutocompleteComponent<CodeEEEPays> implements OnInit {

  types: CodeEEEPays[] = [] as Array<CodeEEEPays>;

  constructor(private formBuild: UntypedFormBuilder, private CodeEEEPaysService: CodeEEEPaysService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: CodeEEEPays[], value: string): CodeEEEPays[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.CodeEEEPaysService.getCodeEEEPays().subscribe(response => this.types = response);
  }

}
