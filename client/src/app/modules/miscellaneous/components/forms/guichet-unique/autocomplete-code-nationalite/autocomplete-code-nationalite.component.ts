import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CodeNationaliteService } from 'src/app/modules/miscellaneous/services/guichet-unique/code.nationalite.service';
import { CodeNationalite } from '../../../../../quotation/model/guichet-unique/referentials/CodeNationalite';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-code-nationalite',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteCodeNationaliteComponent extends GenericLocalAutocompleteComponent<CodeNationalite> implements OnInit {

  types: CodeNationalite[] = [] as Array<CodeNationalite>;

  constructor(private formBuild: UntypedFormBuilder, private CodeNationaliteService: CodeNationaliteService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: CodeNationalite[], value: string): CodeNationalite[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.CodeNationaliteService.getCodeNationalite().subscribe(response => this.types = response);
  }

}
