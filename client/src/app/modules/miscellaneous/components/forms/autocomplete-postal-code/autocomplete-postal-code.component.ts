import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { City } from '../../../model/City';
import { CityService } from '../../../services/city.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-postal-code',
  templateUrl: './autocomplete-postal-code.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./autocomplete-postal-code.component.css']
})
export class AutocompletePostalCodeComponent extends GenericAutocompleteComponent<string, City> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private cityService: CityService, private changeDetectorRef: ChangeDetectorRef) {
    super(formBuild, changeDetectorRef)
  }

  // TODO : marche pas !
  searchEntities(value: string): Observable<City[]> {
    return this.cityService.getCitiesFilteredByPostalCode(value);
  }

  mapResponse(response: City[]): string[] {
    return [...new Set(response.map(city => city.postalCode))]
  }
}
