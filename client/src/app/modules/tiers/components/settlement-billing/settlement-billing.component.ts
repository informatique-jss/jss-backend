import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { instanceOfResponsable, instanceOfTiers } from 'src/app/libs/TypeHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { PaymentType } from 'src/app/modules/miscellaneous/model/PaymentType';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { PaymentTypeService } from 'src/app/modules/miscellaneous/services/payment.type.service';
import { Document } from "../../../miscellaneous/model/Document";
import { ITiers } from '../../model/ITiers';
import { Responsable } from '../../model/Responsable';
import { TiersService } from '../../services/tiers.service';

@Component({
  selector: 'settlement-billing',
  templateUrl: './settlement-billing.component.html',
  styleUrls: ['./settlement-billing.component.css']
})
export class SettlementBillingComponent implements OnInit, AfterContentChecked {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() tiers: ITiers = {} as ITiers;
  @Input() editMode: boolean = false;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;
  paymentTypes: PaymentType[] = [] as Array<PaymentType>;

  paymentTypePrelevement: PaymentType = this.constantService.getPaymentTypePrelevement();
  paymentTypeCheques: PaymentType = this.constantService.getPaymentTypeCheques();
  paymentTypeOther: PaymentType = this.constantService.getPaymentTypeOther();

  refundTypeVirement = this.constantService.getRefundTypeVirement();

  billingLableTypeOther = this.constantService.getBillingLabelTypeOther();

  countryFrance: Country = this.constantService.getCountryFrance();

  billingDocument: Document = {} as Document;
  dunningDocument: Document = {} as Document;
  refundDocument: Document = {} as Document;
  provisionalReceiptDocument: Document = {} as Document;
  billingClosureDocument: Document = {} as Document;
  publicationDocument: Document = {} as Document;
  cfeDocument: Document = {} as Document;
  kbisDocument: Document = {} as Document;

  tiersTypeProspect = this.constantService.getTiersTypeProspect();

  constructor(private formBuilder: UntypedFormBuilder,
    protected paymentTypeService: PaymentTypeService,
    protected tiersService: TiersService,
    protected cityService: CityService,
    private constantService: ConstantService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit() {
    // Referential loading
    this.paymentTypeService.getPaymentTypes().subscribe(response => {
      this.paymentTypes = response;
    })

    // Trigger it to show mandatory fields
    this.settlementBillingForm.markAllAsTouched();
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined) {
      if (instanceOfTiers(this.tiers)) {
        if ((this.tiers.paymentType == null || this.tiers.paymentType == undefined) && this.paymentTypes.length > 0) {
          for (const paymentType of this.paymentTypes) {
            if (paymentType.id == this.paymentTypePrelevement.id)
              this.tiers.paymentType = paymentType;
          }
        }
      }

      this.billingDocument = getDocument(this.constantService.getDocumentTypeBilling(), this.tiers);


      if (!this.billingDocument.billingLabelIsIndividual)
        this.billingDocument.billingLabelIsIndividual = false;

      this.billingClosureDocument = getDocument(this.constantService.getDocumentTypeBillingClosure(), this.tiers);
      if (this.billingClosureDocument.isRecipientClient == null || this.billingClosureDocument.isRecipientClient == false)
        this.billingClosureDocument.isRecipientClient = true;

      if (instanceOfTiers(this.tiers)) {
        this.dunningDocument = getDocument(this.constantService.getDocumentTypeDunning(), this.tiers);
        this.refundDocument = getDocument(this.constantService.getDocumentTypeRefund(), this.tiers);
        this.provisionalReceiptDocument = getDocument(this.constantService.getDocumentTypeProvisionnalReceipt(), this.tiers);
      }
      this.publicationDocument = getDocument(this.constantService.getDocumentTypePublication(), this.tiers);
      this.cfeDocument = getDocument(this.constantService.getDocumentTypeCfe(), this.tiers);
      this.kbisDocument = getDocument(this.constantService.getDocumentTypeKbis(), this.tiers);

    }
    this.settlementBillingForm.markAllAsTouched();
  }

  Validators = Validators;

  settlementBillingForm = this.formBuilder.group({
  });

  instanceOfTiers = instanceOfTiers;
  instanceOfResponsable = instanceOfResponsable;

  getFormStatus(): boolean {
    this.settlementBillingForm.markAllAsTouched();
    return this.settlementBillingForm.valid;
  }

  getResponsables(): Responsable[] {
    let tiers = this.tiersService.getCurrentViewedTiers();
    let responsableViewed = this.tiersService.getCurrentViewedResponsable();
    let responsables = [] as Array<Responsable>;
    if (tiers != null) {
      if (tiers.responsables != null && tiers.responsables.length > 0) {
        tiers.responsables.forEach(responsable => {
          if (responsableViewed != null && responsable.id != responsableViewed.id)
            responsables.push(responsable);
        })
      }
    }
    return responsables;
  }

  fillPostalCode(city: City) {
    if (!this.billingDocument.billingLabelCountry)
      this.billingDocument.billingLabelCountry = city.country;

    if (this.billingDocument.billingLabelCountry.id == this.countryFrance.id && city.postalCode != null)
      this.billingDocument.billingPostalCode = city.postalCode;
  }

  fillCity(postalCode: string) {
    this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
      if (response != null && response != undefined && response.length == 1) {
        let city = response[0];
        if (this.billingDocument.billingLabelCountry == null || this.billingDocument.billingLabelCountry == undefined)
          this.billingDocument.billingLabelCountry = city.country;

        this.billingDocument.billingLabelCity = city;
      }
    })

  }


}
