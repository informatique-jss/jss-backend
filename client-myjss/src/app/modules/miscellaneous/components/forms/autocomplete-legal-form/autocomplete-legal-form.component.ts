import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AutocompleteLibModule } from 'angular-ng-autocomplete';
import { Observable } from 'rxjs';
import { PagedContent } from '../../../../../../../../client/src/app/services/model/PagedContent';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { ConstantService } from '../../../../main/services/constant.service';
import { LegalForm } from '../../../../quotation/model/LegalForm';
import { LegalFormService } from '../../../../quotation/services/legal.form.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-legal-form',
  templateUrl: './../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['./../generic-autocomplete/generic-autocomplete.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, AutocompleteLibModule]
})
export class AutocompleteLegalFormComponent extends GenericAutocompleteComponent<LegalForm, LegalForm> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private legalformService: LegalFormService, private constantService: ConstantService) {
    super(formBuild)
  }

  searchEntities(value: string): Observable<PagedContent<LegalForm>> {
    return this.legalformService.getLegalFormsFilteredByName(value, this.page, this.pageSize);
  }

  override optionSelected(type: LegalForm): void {
    super.optionSelected(type);
    this.onFormChange.next();
  }
}
