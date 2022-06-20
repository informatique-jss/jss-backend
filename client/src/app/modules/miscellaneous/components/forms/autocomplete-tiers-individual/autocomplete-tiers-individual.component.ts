import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-tiers-individual',
  templateUrl: './autocomplete-tiers-individual.component.html',
  styleUrls: ['./autocomplete-tiers-individual.component.css']
})
export class AutocompleteTiersIndividualComponent extends GenericAutocompleteComponent<Tiers, Tiers> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder,
    private tiersService: TiersService, private changeDetectorRef: ChangeDetectorRef) {
    super(formBuild, changeDetectorRef)
  }

  searchEntities(value: string): Observable<Tiers[]> {
    this.expectedMinLengthInput = 14;
    return this.tiersService.getIndividualTiersByKeyword(value);
  }

  displayTiers(tiers: Tiers): string {
    if (!tiers)
      return "";
    if (tiers.denomination)
      return tiers.denomination!;
    return tiers.firstname + " " + tiers.lastname;
  }
}
