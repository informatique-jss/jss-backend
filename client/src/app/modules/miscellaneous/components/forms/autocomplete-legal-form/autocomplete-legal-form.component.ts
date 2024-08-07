
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { LegalForm } from '../../../model/LegalForm';
import { LegalFormService } from '../../../services/legal.form.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-legal-form',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteLegalFormComponent extends GenericLocalAutocompleteComponent<LegalForm> implements OnInit {

  types: LegalForm[] = [] as Array<LegalForm>;

  constructor(private formBuild: UntypedFormBuilder, private legalformService: LegalFormService, private appService3: AppService) {
    super(formBuild, appService3)
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

  displayLabel(object: any): string {
    return object ? object.label + " - " + object.description : '';
  }

}
