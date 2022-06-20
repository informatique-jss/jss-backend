import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Country } from '../../../model/Country';
import { CountryService } from '../../../services/country.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-country',
  templateUrl: './autocomplete-country.component.html',
  styleUrls: ['./autocomplete-country.component.css']
})
export class AutocompleteCountryComponent extends GenericLocalAutocompleteComponent<Country> implements OnInit {

  types: Country[] = [] as Array<Country>;

  constructor(private formBuild: FormBuilder, private countryService: CountryService) {
    super(formBuild)
  }

  filterEntities(types: Country[], value: string): Country[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(country =>
      country.label != undefined && (country.label.toLowerCase().includes(filterValue)));
  }

  initTypes(): void {
    this.countryService.getCountries().subscribe(response => this.types = response);
  }
}
