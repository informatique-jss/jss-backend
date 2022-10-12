import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { COUNTRY_CODE_FRANCE } from 'src/app/libs/Constants';
import { BillingCenter } from 'src/app/modules/miscellaneous/model/BillingCenter';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { BillingCenterService } from 'src/app/modules/miscellaneous/services/billing.center.service';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-billing-center',
  templateUrl: './referential-billing-center.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBillingCenterComponent extends GenericReferentialComponent<BillingCenter> implements OnInit {
  constructor(private billingCenterService: BillingCenterService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private cityService: CityService) {
    super(formBuilder2, appService2);
  }

  COUNTRY_CODE_FRANCE = COUNTRY_CODE_FRANCE;

  getAddOrUpdateObservable(): Observable<BillingCenter> {
    return this.billingCenterService.addOrUpdateBillingCenter(this.selectedEntity!);
  }
  getGetObservable(): Observable<BillingCenter[]> {
    return this.billingCenterService.getBillingCenters();
  }

  fillPostalCode(city: City) {
    if (this.selectedEntity) {
      if (this.selectedEntity.country == null || this.selectedEntity.country == undefined)
        this.selectedEntity.country = city.country;

      if (this.selectedEntity.country.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
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
