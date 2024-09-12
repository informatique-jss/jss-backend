import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Regie } from '../../../model/Regie';
import { RegieService } from '../../../services/regie.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-regie',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteRegieComponent extends GenericLocalAutocompleteComponent<Regie> implements OnInit {

  types: Regie[] = [] as Array<Regie>;

  constructor(private formBuild: UntypedFormBuilder, private regieService: RegieService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: Regie[], value: string): Regie[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(regie => regie && regie.label.toLowerCase().includes(filterValue));
  }

  initTypes(): void {
    this.regieService.getRegies().subscribe(response => this.types = response);
  }
}
