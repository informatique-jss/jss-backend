import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Siret } from 'src/app/modules/quotation/model/Siret';
import { SiretService } from 'src/app/modules/quotation/services/siret.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-siret',
  templateUrl: './autocomplete-siret.component.html',
  styleUrls: ['./autocomplete-siret.component.css']
})
export class AutocompleteSiretComponent extends GenericAutocompleteComponent<Siret, Siret> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder,
    private siretService: SiretService, private changeDetectorRef: ChangeDetectorRef) {
    super(formBuild, changeDetectorRef)
  }

  searchEntities(value: string): Observable<Siret[]> {
    this.expectedMinLengthInput = 14;
    return this.siretService.getSiret(value);
  }

  displaySiret(siret: Siret): string {
    if (!siret)
      return "";
    if (!siret.etablissement)
      return siret + "";
    return siret.etablissement.siret;
  }
}
