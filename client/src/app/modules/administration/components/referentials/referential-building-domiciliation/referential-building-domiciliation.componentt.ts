import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { BuildingDomiciliation } from 'src/app/modules/quotation/model/BuildingDomiciliation';
import { BuildingDomiciliationService } from 'src/app/modules/quotation/services/building.domiciliation.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-building-domiciliation',
  templateUrl: 'referential-building-domiciliation.componentt.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBuildingDomiciliationComponent extends GenericReferentialComponent<BuildingDomiciliation> implements OnInit {
  constructor(private buildingDomiciliationService: BuildingDomiciliationService,
    private cityService: CityService,
    private constantService: ConstantService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<BuildingDomiciliation> {
    return this.buildingDomiciliationService.addOrUpdateBuildingDomiciliation(this.selectedEntity!);
  }
  getGetObservable(): Observable<BuildingDomiciliation[]> {
    return this.buildingDomiciliationService.getBuildingDomiciliations();
  }


  fillPostalCode(city: City) {
    if (this.selectedEntity! != null) {
      if (this.selectedEntity!.country == null || this.selectedEntity!.country == undefined)
        this.selectedEntity!.country = city.country;

      if (this.selectedEntity!.country.id == this.constantService.getCountryFrance().id && city.postalCode != null && !this.selectedEntity!.postalCode)
        this.selectedEntity!.postalCode = city.postalCode;
    }
  }

  fillCity(postalCode: string) {
    if (this.selectedEntity! != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.selectedEntity! != null) {
            if (this.selectedEntity!.country == null || this.selectedEntity!.country == undefined)
              this.selectedEntity!.country = city.country;

            this.selectedEntity!.city = city;
          }
        }
      })
    }
  }
}
