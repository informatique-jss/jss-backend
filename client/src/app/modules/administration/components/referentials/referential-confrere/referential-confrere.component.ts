import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { Observable } from 'rxjs';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { PaymentType } from 'src/app/modules/miscellaneous/model/PaymentType';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { PaymentTypeService } from 'src/app/modules/miscellaneous/services/payment.type.service';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { ConfrereService } from 'src/app/modules/quotation/services/confrere.service';
import { RefundType } from 'src/app/modules/tiers/model/RefundType';
import { AppService } from 'src/app/services/app.service';
import { Document } from "../../../../miscellaneous/model/Document";
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-confrere',
  templateUrl: 'referential-confrere.component.html',
  styleUrls: ['referential-confrere.component.css']
})
export class ReferentialConfrereComponent extends GenericReferentialComponent<Confrere> implements OnInit {
  constructor(private confrereService: ConfrereService,
    private cityService: CityService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private constantService: ConstantService,
    protected paymentTypeService: PaymentTypeService,) {
    super(formBuilder2, appService2);
  }

  grades: string[] = ["+", "++", "+++", "++++", "+++++"];

  paymentTypes: PaymentType[] = [] as Array<PaymentType>;

  paymentTypePrelevement = this.constantService.getPaymentTypePrelevement();
  paymentTypeCheques = this.constantService.getPaymentTypeCheques();
  paymentTypeOther = this.constantService.getPaymentTypeOther();


  billingLabelTypeOther = this.constantService.getBillingLabelTypeOther();

  refundTypeVirement: RefundType = this.constantService.getRefundTypeVirement();

  billingDocument: Document = {} as Document;
  dunningDocument: Document = {} as Document;
  refundDocument: Document = {} as Document;

  countryFrance = this.constantService.getCountryFrance();

  Validators = Validators;

  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  selectEntity(element: Confrere) {
    this.selectedEntity = element;
    this.selectedEntityChange.emit(this.selectedEntity);

    this.billingDocument = getDocument(this.constantService.getDocumentTypeBilling(), this.selectedEntity!);
    this.dunningDocument = getDocument(this.constantService.getDocumentTypeDunning(), this.selectedEntity!);
    this.refundDocument = getDocument(this.constantService.getDocumentTypeRefund(), this.selectedEntity!);
    this.entityForm.markAllAsTouched();
  }

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

  mapEntities(): void {
    this.paymentTypeService.getPaymentTypes().subscribe(response => {
      this.paymentTypes = response;
    })
  }

  fillPostalCode(city: City) {
    if (this.selectedEntity! != null) {
      if (this.selectedEntity!.country == null || this.selectedEntity!.country == undefined)
        this.selectedEntity!.country = city.country;

      if (this.selectedEntity!.country.id == this.countryFrance.id && city.postalCode != null)
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
