import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { ResponsableService } from 'src/app/modules/tiers/services/responsable.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-responsable',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteResponsableComponent extends GenericAutocompleteComponent<Responsable, Responsable> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private responsableService: ResponsableService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<Responsable[]> {
    this.expectedMinLengthInput = 14;
    return this.responsableService.getResponsableByKeyword(value);
  }

  displayLabel(responsable: Responsable): string {
    if (!responsable)
      return "";
    return responsable.firstname + " " + responsable.lastname;
  }
}
