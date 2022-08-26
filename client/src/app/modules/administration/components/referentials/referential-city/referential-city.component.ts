import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-city',
  templateUrl: 'referential-city.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialCityComponent extends GenericReferentialComponent<City> implements OnInit {
  constructor(private cityService: CityService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<City> {
    return this.cityService.addOrUpdateCity(this.selectedEntity!);
  }
  getGetObservable(): Observable<City[]> {
    return this.cityService.getCities();
  }
}
