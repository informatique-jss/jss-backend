import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Country } from '../../../model/Country';
import { ConstantService } from '../../../services/constant.service';
import { CountryService } from '../../../services/country.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-country',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteCountryComponent extends GenericLocalAutocompleteComponent<Country> implements OnInit {

  types: Country[] = [] as Array<Country>;
  @Input() defaultCountry: Country = this.constantService.getCountryFrance();

  constructor(private formBuild: UntypedFormBuilder, private countryService: CountryService,
    private constantService: ConstantService,) {
    super(formBuild)
  }

  ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
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
          if (country.id == this.constantService.getCountryFrance().id)
            this.model = country;
    });
  }
}
