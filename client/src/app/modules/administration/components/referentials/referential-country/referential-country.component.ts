import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { CountryService } from 'src/app/modules/miscellaneous/services/country.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-country',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialCountryComponent extends GenericReferentialComponent<Country> implements OnInit {
  constructor(private countryService: CountryService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<Country> {
    return this.countryService.addOrUpdateCountry(this.selectedEntity!);
  }
  getGetObservable(): Observable<Country[]> {
    return this.countryService.getCountries();
  }
}
