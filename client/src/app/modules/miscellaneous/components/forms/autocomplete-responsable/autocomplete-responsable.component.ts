import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { ResponsableService } from 'src/app/modules/tiers/services/responsable.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-responsable',
  templateUrl: './autocomplete-responsable.component.html',
  styleUrls: ['./autocomplete-responsable.component.css']
})
export class AutocompleteResponsableComponent extends GenericAutocompleteComponent<Responsable, Responsable> implements OnInit {

  constructor(private formBuild: FormBuilder,
    private responsableService: ResponsableService, private changeDetectorRef: ChangeDetectorRef) {
    super(formBuild, changeDetectorRef)
  }

  searchEntities(value: string): Observable<Responsable[]> {
    this.expectedMinLengthInput = 14;
    return this.responsableService.getResponsableByKeyword(value);
  }

  displayResponsable(responsable: Responsable): string {
    if (!responsable)
      return "";
    return responsable.firstname + " " + responsable.lastname;
  }
}
