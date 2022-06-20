
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { LegalForm } from '../../../model/LegalForm';
import { LegalFormService } from '../../../services/legal.form.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-legal-form',
  templateUrl: './autocomplete-legal-form.component.html',
  styleUrls: ['./autocomplete-legal-form.component.css']
})
export class AutocompleteLegalFormComponent extends GenericLocalAutocompleteComponent<LegalForm> implements OnInit {

  types: LegalForm[] = [] as Array<LegalForm>;

  constructor(private formBuild: UntypedFormBuilder, private legalformService: LegalFormService) {
    super(formBuild)
  }

  filterEntities(types: LegalForm[], value: string): LegalForm[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(legalform =>
      legalform.label != undefined
      && (legalform.label.toLowerCase().includes(filterValue)));
  }

  initTypes(): void {
    this.legalformService.getLegalForms().subscribe(response => this.types = response);
  }
}
