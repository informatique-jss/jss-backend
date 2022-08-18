import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { COUNTRY_CODE_FRANCE } from 'src/app/libs/Constants';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { Regie } from 'src/app/modules/quotation/model/Regie';
import { RegieService } from 'src/app/modules/quotation/services/regie.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-regie',
  templateUrl: 'referential-regie.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialRegieComponent extends GenericReferentialComponent<Regie> implements OnInit {
  constructor(private regieService: RegieService,
    private cityService: CityService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }
  getAddOrUpdateObservable(): Observable<Regie> {
    return this.regieService.addOrUpdateRegie(this.selectedEntity!);
  }
  getGetObservable(): Observable<Regie[]> {
    return this.regieService.getRegies();
  }


  limitTextareaSize(numberOfLine: number) {
    if (this.selectedEntity?.mailRecipient != null) {
      var l = this.selectedEntity?.mailRecipient.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
      var outValue = "";
      if (l.length > numberOfLine) {
        outValue = l.slice(0, numberOfLine).join("\n");
        this.selectedEntity.mailRecipient = outValue;
      }
    }
  }


  fillPostalCode(city: City) {
    if (this.selectedEntity! != null) {
      if (this.selectedEntity!.country == null || this.selectedEntity!.country == undefined)
        this.selectedEntity!.country = city.country;

      if (this.selectedEntity!.country.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
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
