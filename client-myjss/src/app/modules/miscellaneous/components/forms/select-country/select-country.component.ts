import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { Country } from '../../../../profile/model/Country';
import { CountryService } from '../../../../quotation/services/country.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-country',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectCountryComponent extends GenericSelectComponent<Country> implements OnInit {

  @Input() types: Country[] = [] as Array<Country>;

  constructor(private formBuild: UntypedFormBuilder,
    private countryService: CountryService) {
    super(formBuild)
  }

  initTypes(): void {
    this.countryService.getCountries().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label));
    })
  }
}
