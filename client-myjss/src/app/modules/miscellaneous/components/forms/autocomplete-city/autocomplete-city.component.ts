import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AutocompleteLibModule } from 'angular-ng-autocomplete';
import { Observable } from 'rxjs';
import { PagedContent } from '../../../../../../../../client/src/app/services/model/PagedContent';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { ConstantService } from '../../../../main/services/constant.service';
import { City } from '../../../../profile/model/City';
import { Country } from '../../../../profile/model/Country';
import { CityService } from '../../../../quotation/services/city.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-city',
  templateUrl: './../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['./../generic-autocomplete/generic-autocomplete.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, AutocompleteLibModule]
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
  @Input() preFilterPostalCode: string = '';

  constructor(private formBuild: UntypedFormBuilder, private cityService: CityService, private constantService: ConstantService) {
    super(formBuild)
  }

  searchEntities(value: string): Observable<PagedContent<City>> {
    if (!this.modelCountry)
      this.modelCountry = this.constantService.getCountryFrance();

    return this.cityService.getCitiesFilteredByNameAndCountryAndPostalCode(value, this.modelCountry, '', this.page, this.pageSize);
  }

  override optionSelected(type: City): void {
    super.optionSelected(type);
    this.onFormChange.next();
  }

}
