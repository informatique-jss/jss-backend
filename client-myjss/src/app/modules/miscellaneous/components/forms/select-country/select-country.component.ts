import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Country } from '../../../../profile/model/Country';
import { CountryService } from '../../../../quotation/services/country.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-country',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})
export class SelectCountryComponent extends GenericSelectComponent<Country> implements OnInit {

  @Input() types: Country[] = [] as Array<Country>;

  constructor(private formBuild: UntypedFormBuilder,
    private countryService: CountryService) {
    super(formBuild)
  }

  initTypes(): void {
    this.countryService.getCountries().subscribe(response => {
      this.types = response;
    })
  }
}
