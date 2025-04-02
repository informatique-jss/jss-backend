import { ChangeDetectionStrategy, Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ConstantService } from '../../../../../libs/constant.service';
import { City } from '../../../../profile/model/City';
import { Country } from '../../../../profile/model/Country';
import { CityService } from '../../../../quotation/services/city.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-city',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false
})
export class AutocompleteCityComponent extends GenericAutocompleteComponent<City, City> implements OnInit, OnChanges {

  /**
   * The model of country property.
   * If undefined, cities are searched worldwide
   */
  @Input() modelCountry: Country | undefined;

  /**
   * Bind a model here to prefilter cities with a given postal code
   * On model change, city search is triggered
   */
  @Input() preFilterPostalCode: string = "";

  constructor(private formBuild: UntypedFormBuilder, private cityService: CityService, private constantService: ConstantService) {
    super(formBuild)
  }

  searchEntities(value: string): Observable<City[]> {
    if (this.modelCountry)
      return this.cityService.getCitiesFilteredByCountryAndNameAndPostalCode(this.modelCountry, this.preFilterPostalCode);
    return this.cityService.getCitiesFilteredByCountryAndNameAndPostalCode(this.constantService.getCountryFrance(), this.preFilterPostalCode);
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    console.log(this.model);
    if (changes && changes['preFilterPostalCode'] && this.preFilterPostalCode && this.preFilterPostalCode.length > 2 && !this.model)
      this.searchEntities("").subscribe(response => this.filteredTypes = response);
  }
}
