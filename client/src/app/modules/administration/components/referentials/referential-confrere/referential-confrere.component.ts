import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { Observable } from 'rxjs';
import { BILLING_TIERS_DOCUMENT_TYPE_BILLING_CENTER, BILLING_TIERS_DOCUMENT_TYPE_CODE, COUNTRY_CODE_FRANCE, DUNNING_TIERS_DOCUMENT_TYPE_CODE, PAYMENT_TYPE_CHEQUES, PAYMENT_TYPE_OTHERS, PAYMENT_TYPE_PRELEVEMENT, REFUND_TIERS_DOCUMENT_TYPE_CODE, REFUND_TYPE_VIREMENT } from 'src/app/libs/Constants';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { PaymentType } from 'src/app/modules/miscellaneous/model/PaymentType';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { DocumentTypeService } from 'src/app/modules/miscellaneous/services/document.type.service';
import { PaymentTypeService } from 'src/app/modules/miscellaneous/services/payment.type.service';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { ConfrereService } from 'src/app/modules/quotation/services/confrere.service';
import { AppService } from 'src/app/services/app.service';
import { Document } from "../../../../miscellaneous/model/Document";
import { DocumentType } from "../../../../miscellaneous/model/DocumentType";
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
    protected paymentTypeService: PaymentTypeService,
    protected documentTypeService: DocumentTypeService,) {
    super(formBuilder2, appService2);
  }

  grades: string[] = ["+", "++", "+++", "++++", "+++++"];

  paymentTypes: PaymentType[] = [] as Array<PaymentType>;
  PAYMENT_TYPE_PRELEVEMENT = PAYMENT_TYPE_PRELEVEMENT;
  PAYMENT_TYPE_CHEQUES = PAYMENT_TYPE_CHEQUES;
  PAYMENT_TYPE_OTHERS = PAYMENT_TYPE_OTHERS;

  REFUND_TYPE_VIREMENT = REFUND_TYPE_VIREMENT;
  BILLING_TIERS_DOCUMENT_TYPE_BILLING_CENTER = BILLING_TIERS_DOCUMENT_TYPE_BILLING_CENTER;

  documentTypes: DocumentType[] = [] as Array<DocumentType>;
  billingDocument: Document = {} as Document;
  dunningDocument: Document = {} as Document;
  refundDocument: Document = {} as Document;

  COUNTRY_CODE_FRANCE = COUNTRY_CODE_FRANCE;

  Validators = Validators;

  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  selectEntity(element: Confrere) {
    this.selectedEntity = element;
    this.selectedEntityChange.emit(this.selectedEntity);
    this.documentTypeService.getDocumentTypes().subscribe(response => {
      this.documentTypes = response;

      this.billingDocument = getDocument(BILLING_TIERS_DOCUMENT_TYPE_CODE, this.selectedEntity!, this.documentTypes);
      this.dunningDocument = getDocument(DUNNING_TIERS_DOCUMENT_TYPE_CODE, this.selectedEntity!, this.documentTypes);
      this.refundDocument = getDocument(REFUND_TIERS_DOCUMENT_TYPE_CODE, this.selectedEntity!, this.documentTypes);
      this.entityForm.markAllAsTouched();
    })
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
