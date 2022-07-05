import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { COUNTRY_CODE_FRANCE } from 'src/app/libs/Constants';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { ConfrereService } from 'src/app/modules/quotation/services/confrere.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-confrere',
  templateUrl: 'referential-confrere.component.html',
  styleUrls: ['referential-confrere.component.css']
})
export class ReferentialConfrereComponent extends GenericReferentialComponent<Confrere> implements OnInit {
  constructor(private confrereService: ConfrereService,
    private cityService: CityService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  grades: string[] = ["+", "++", "+++", "++++", "+++++"];

  getAddOrUpdateObservable(): Observable<Confrere> {
    return this.confrereService.addOrUpdateConfrere(this.selectedEntity!);
  }
  getGetObservable(): Observable<Confrere[]> {
    return this.confrereService.getConfreres();
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

  fillAccountCustomer() {
    if (this.selectedEntity && !this.selectedEntity.accountingAccountCustomer)
      this.selectedEntity.accountingAccountCustomer = this.selectedEntity.accountingAccountProvider;
  }

  fillAccountProvider() {
    if (this.selectedEntity && !this.selectedEntity.accountingAccountProvider)
      this.selectedEntity.accountingAccountProvider = this.selectedEntity.accountingAccountCustomer;
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
