import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Regie } from '../../../model/Regie';
import { RegieService } from '../../../services/regie.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-regie',
  templateUrl: './autocomplete-regie.component.html',
  styleUrls: ['./autocomplete-regie.component.css']
})
export class AutocompleteRegieComponent extends GenericLocalAutocompleteComponent<Regie> implements OnInit {

  types: Regie[] = [] as Array<Regie>;

  constructor(private formBuild: UntypedFormBuilder, private regieService: RegieService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  filterEntities(types: Regie[], value: string): Regie[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(regie => regie && regie.label.toLowerCase().includes(filterValue));
  }

  initTypes(): void {
    this.regieService.getRegies().subscribe(response => this.types = response);
  }
}
