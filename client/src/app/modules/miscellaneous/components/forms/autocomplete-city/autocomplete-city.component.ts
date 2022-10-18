import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
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
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteCityComponent extends GenericAutocompleteComponent<City, City> implements OnInit {

  /**
   * The model of country property.
   * If undefined, cities are searched worldwide
   */
  @Input() modelCountry: Country | undefined;

  constructor(private formBuild: UntypedFormBuilder, private cityService: CityService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<City[]> {
    return this.cityService.getCitiesFilteredByCountryAndName(value, this.modelCountry);
  }
}
