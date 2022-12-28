import { ChangeDetectionStrategy, Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { City } from '../../../model/City';
import { Country } from '../../../model/Country';
import { CityService } from '../../../services/city.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-city',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AutocompleteCityComponent extends GenericAutocompleteComponent<City, City> implements OnInit {

  /**
   * The model of country property.
   * If undefined, cities are searched worldwide
   */
  @Input() modelCountry: Country | undefined;

  /**
   * Bind a model here to prefilter cities with a given postal code
   * On model change, city search is triggered
   */
  @Input() preFilterPostalCode: string | undefined;

  constructor(private formBuild: UntypedFormBuilder, private cityService: CityService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<City[]> {
    return this.cityService.getCitiesFilteredByCountryAndNameAndPostalCode(value, this.modelCountry, this.preFilterPostalCode);
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    if (changes && changes.preFilterPostalCode && this.preFilterPostalCode && this.preFilterPostalCode.length > 2)
      this.searchEntities("").subscribe(response => this.filteredTypes = response);
  }
}
