import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { Regie } from 'src/app/modules/miscellaneous/model/Regie';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { RegieService } from 'src/app/modules/miscellaneous/services/regie.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
@Component({
  selector: 'referential-regie',
  templateUrl: './referential-regie.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialRegieComponent extends GenericReferentialComponent<Regie> implements OnInit {
  constructor(private regieService: RegieService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private constantService: ConstantService,
    private cityService: CityService) {
    super(formBuilder2, appService2);
  }

  countryFrance: Country = this.constantService.getCountryFrance();

  getAddOrUpdateObservable(): Observable<Regie> {
    return this.regieService.addOrUpdateRegie(this.selectedEntity!);
  }
  getGetObservable(): Observable<Regie[]> {
    return this.regieService.getRegies();
  }

  fillPostalCode(city: City) {
    if (this.selectedEntity) {
      if (this.selectedEntity.country == null || this.selectedEntity.country == undefined)
        this.selectedEntity.country = city.country;

      if (this.selectedEntity.country.id == this.countryFrance.id && city.postalCode != null)
        this.selectedEntity.postalCode = city.postalCode;
    }
  }

  fillCity(postalCode: string) {
    if (this.selectedEntity) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.selectedEntity) {
            if (this.selectedEntity.country == null || this.selectedEntity.country == undefined)
              this.selectedEntity.country = city.country;

            this.selectedEntity.city = city;
          }
        }
      })
    }
  }
}
