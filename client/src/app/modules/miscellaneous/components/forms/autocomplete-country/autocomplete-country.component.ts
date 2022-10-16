import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { COUNTRY_CODE_FRANCE } from 'src/app/libs/Constants';
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
  @Input() defaultCountryCode: string = COUNTRY_CODE_FRANCE;

  constructor(private formBuild: UntypedFormBuilder, private countryService: CountryService) {
    super(formBuild)
  }

  filterEntities(types: Country[], value: string): Country[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(country =>
      country.label != undefined && (country.label.toLowerCase().includes(filterValue)));
  }

  initTypes(): void {
    this.countryService.getCountries().subscribe(response => {
      this.types = response;
      if (this.types)
        for (let country of this.types)
          if (country.code == COUNTRY_CODE_FRANCE)
            this.model = country;
    });
  }
}
