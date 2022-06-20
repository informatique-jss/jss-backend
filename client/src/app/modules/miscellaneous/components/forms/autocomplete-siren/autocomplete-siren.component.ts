import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Siren } from 'src/app/modules/quotation/model/Siren';
import { SirenService } from 'src/app/modules/quotation/services/siren.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-siren',
  templateUrl: './autocomplete-siren.component.html',
  styleUrls: ['./autocomplete-siren.component.css']
})
export class AutocompleteSirenComponent extends GenericAutocompleteComponent<Siren, Siren> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder,
    private sirenService: SirenService, private changeDetectorRef: ChangeDetectorRef) {
    super(formBuild, changeDetectorRef)
  }

  searchEntities(value: string): Observable<Siren[]> {
    this.expectedMinLengthInput = 14;
    return this.sirenService.getSiren(value);
  }

  displaySiren(siren: Siren): string {
    if (!siren)
      return "";
    if (!siren.uniteLegale)
      return siren + "";
    return siren.uniteLegale.siren;
  }
}
