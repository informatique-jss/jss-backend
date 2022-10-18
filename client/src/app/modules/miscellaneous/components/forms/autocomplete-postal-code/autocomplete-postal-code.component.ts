import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { City } from '../../../model/City';
import { CityService } from '../../../services/city.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-postal-code',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompletePostalCodeComponent extends GenericAutocompleteComponent<string, City> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private cityService: CityService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<City[]> {
    return this.cityService.getCitiesFilteredByPostalCode(value);
  }

  mapResponse(response: City[]): string[] {
    return [...new Set(response.map(city => city.postalCode))]
  }
  displayLabel(object: any): string {
    return object;
  }
}
