import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { BILLING_CLOSURE_TIERS_DOCUMENT_TYPE_CODE, BILLING_TIERS_DOCUMENT_TYPE_CODE, BILLING_TIERS_DOCUMENT_TYPE_OTHER_CODE, DUNNING_TIERS_DOCUMENT_TYPE_CODE, PAYMENT_TYPE_PRELEVEMENT, PROVISIONAL_RECEIPT_TIERS_DOCUMENT_TYPE_CODE, REFUND_TIERS_DOCUMENT_TYPE_CODE, REFUND_TYPE_VIREMENT, SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { validateEmail } from 'src/app/libs/CustomFormsValidatorsHelper';
import { prepareMail } from 'src/app/libs/MailHelper';
import { PaymentType } from 'src/app/modules/miscellaneous/model/PaymentType';
import { PaymentTypeService } from 'src/app/modules/miscellaneous/services/payment-type.service';
import { BillingClosureRecipientType } from '../../model/BillingClosureRecipientType';
import { BillingClosureType } from '../../model/BillingClosureType';
import { BillingLabelType } from '../../model/BillingLabelType';
import { ITiers } from '../../model/ITiers';
import { Tiers } from '../../model/Tiers';
import { Mail } from '../../model/Mail';
import { PaymentDeadlineType } from '../../model/PaymentDeadlineType';
import { RefundType } from '../../model/RefundType';
import { TiersDocument } from '../../model/TiersDocument';
import { TiersDocumentType } from '../../model/TiersDocumentType';
import { BillingClosureRecipientTypeService } from '../../services/billing.closure.recipient.type.service';
import { BillingClosureTypeService } from '../../services/billing.closure.type.service';
import { BillingLabelTypeService } from '../../services/billing.label.type.service';
import { PaymentDeadlineTypeService } from '../../services/payment.deadline.type.service';
import { RefundTypeService } from '../../services/refund.type.service';
import { TiersComponent } from '../tiers/tiers.component';
import { TiersDocumentTypeService } from './../../services/tiers.document.type.service';
import { Responsable } from '../../model/Responsable';
import { TiersService } from '../../services/tiers.service';

@Component({
  selector: 'settlement-billing',
  templateUrl: './settlement-billing.component.html',
  styleUrls: ['./settlement-billing.component.css']
})
export class SettlementBillingComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() tiers: ITiers = {} as ITiers;
  @Input() editMode: boolean = false;
  paymentTypes: PaymentType[] = [] as Array<PaymentType>;
  paymentDeadlineTypes: PaymentDeadlineType[] = [] as Array<PaymentDeadlineType>;
  refundTypes: RefundType[] = [] as Array<RefundType>;
  billingClosureTypes: BillingClosureType[] = [] as Array<BillingClosureType>;
  billingClosureRecipientTypes: BillingClosureRecipientType[] = [] as Array<BillingClosureRecipientType>;
  PAYMENT_TYPE_PRELEVEMENT = PAYMENT_TYPE_PRELEVEMENT;
  REFUND_TYPE_VIREMENT = REFUND_TYPE_VIREMENT;
  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;

  tiersDocumentTypes: TiersDocumentType[] = [] as Array<TiersDocumentType>;
  billingLabelTypes: BillingLabelType[] = [] as Array<BillingLabelType>;
  billingDocument: TiersDocument = {} as TiersDocument;
  dunningDocument: TiersDocument = {} as TiersDocument;
  refundDocument: TiersDocument = {} as TiersDocument;
  provisionalReceiptDocument: TiersDocument = {} as TiersDocument;
  billingClosureDocument: TiersDocument = {} as TiersDocument;

  overrideBillingAffaireAddress: boolean = false;
  overrideBillingAffaireMail: boolean = false;
  overrideBillingClientAddress: boolean = false;
  overrideBillingClientMail: boolean = false;

  overrideDunningAffaireAddress: boolean = false;
  overrideDunningAffaireMail: boolean = false;
  overrideDunningClientAddress: boolean = false;
  overrideDunningClientMail: boolean = false;

  overrideRefundAffaireAddress: boolean = false;
  overrideRefundAffaireMail: boolean = false;
  overrideRefundClientAddress: boolean = false;
  overrideRefundClientMail: boolean = false;

  overrideBillingClosureClientAddress: boolean = false;
  overrideBillingClosureClientMail: boolean = false;

  overrideProvisionalReceiptAffaireMail: boolean = false;
  overrideProvisionalReceiptClientMail: boolean = false;

  BILLING_TIERS_DOCUMENT_TYPE_OTHER_CODE = BILLING_TIERS_DOCUMENT_TYPE_OTHER_CODE;
  DUNNING_TIERS_DOCUMENT_TYPE_CODE = DUNNING_TIERS_DOCUMENT_TYPE_CODE;
  REFUND_TIERS_DOCUMENT_TYPE_CODE = REFUND_TIERS_DOCUMENT_TYPE_CODE;
  BILLING_CLOSURE_TIERS_DOCUMENT_TYPE_CODE = BILLING_CLOSURE_TIERS_DOCUMENT_TYPE_CODE;
  PROVISIONAL_RECEIPT_TIERS_DOCUMENT_TYPE_CODE = PROVISIONAL_RECEIPT_TIERS_DOCUMENT_TYPE_CODE;

  constructor(private formBuilder: FormBuilder,
    protected paymentTypeService: PaymentTypeService,
    protected tiersDocumentTypeService: TiersDocumentTypeService,
    protected billingLabelTypeService: BillingLabelTypeService,
    protected paymentDeadlineTypeService: PaymentDeadlineTypeService,
    protected tiersService: TiersService,
    protected refundTypeService: RefundTypeService,
    protected billingClosureTypeService: BillingClosureTypeService,
    protected billingClosureRecipientTypeService: BillingClosureRecipientTypeService,
  ) { }

  ngOnInit() {
    // Referential loading
    this.paymentTypeService.getPaymentTypes().subscribe(response => {
      this.paymentTypes = response;
    })
    this.billingLabelTypeService.getBillingLabelTypes().subscribe(response => {
      this.billingLabelTypes = response;
    })
    this.paymentDeadlineTypeService.getPaymentDeadlineTypes().subscribe(response => {
      this.paymentDeadlineTypes = response;
    })
    this.refundTypeService.getRefundTypes().subscribe(response => {
      this.refundTypes = response;
    })
    this.billingClosureTypeService.getBillingClosureTypes().subscribe(response => {
      this.billingClosureTypes = response;
    })
    this.billingClosureRecipientTypeService.getBillingClosureRecipientTypes().subscribe(response => {
      this.billingClosureRecipientTypes = response;
    })

    // Trigger it to show mandatory fields
    this.settlementBillingForm.markAllAsTouched();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined) {
      if (TiersComponent.instanceOfTiers(this.tiers)) {
        if ((this.tiers.paymentType == null || this.tiers.paymentType != undefined) && this.paymentTypes.length > 0) {
          this.paymentTypes.forEach(paymentType => {
            if (paymentType.code == PAYMENT_TYPE_PRELEVEMENT)
              if (TiersComponent.instanceOfTiers(this.tiers))
                this.tiers.paymentType = paymentType;
          })
        }

        if (this.tiers.isProvisionalPaymentMandatory == undefined || this.tiers.isProvisionalPaymentMandatory == null)
          this.tiers.isProvisionalPaymentMandatory = false;
      }

      this.tiersDocumentTypeService.getDocumentTypes().subscribe(response => {
        this.tiersDocumentTypes = response;

        this.billingDocument = TiersComponent.getDocument(BILLING_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
        this.billingClosureDocument = TiersComponent.getDocument(BILLING_CLOSURE_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
        if (this.billingClosureDocument.isRecipientClient == null || this.billingClosureDocument.isRecipientClient == false)
          this.billingClosureDocument.isRecipientClient = true;

        if (TiersComponent.instanceOfTiers(this.tiers)) {
          this.dunningDocument = TiersComponent.getDocument(DUNNING_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
          this.refundDocument = TiersComponent.getDocument(REFUND_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
          this.provisionalReceiptDocument = TiersComponent.getDocument(PROVISIONAL_RECEIPT_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
        }

      })
    }
    this.settlementBillingForm.markAllAsTouched();
  }

  settlementBillingForm = this.formBuilder.group({
    paymentType: [''],
    paymentIBAN: ['', [Validators.maxLength(40), this.checkFieldFilledIfPaymentIsPrelevement("paymentIBAN")]],
    isProvisionalPaymentMandatory: ['', Validators.required],
    billingLabelType: [''],
    billingLabel: ['', Validators.maxLength(40)],
    isResponsableOnBilling: [''],
    isCommandNumberMandatory: [''],
    commandNumber: ['', Validators.maxLength(40)],
    isAutomaticDunning: [''],
    paymentDeadlineType: [''],
    refundIBAN: ['', [this.checkFieldFilledIfRefundIsPrelevement("refundIBAN"), Validators.maxLength(40)]],
    isRefundable: [''],
    refundType: [''],
    billingClosureType: [''],
    billingClosureRecipientType: [''],
    mailsCreationAffaire: [''],
    mailsProvisionningConfirmation: [''],
    mailsMissingItemFormality: [''],
  });

  instanceOfTiers = TiersComponent.instanceOfTiers;
  instanceOfResponsable = TiersComponent.instanceOfResponsable;

  // Check if the propertiy given in parameter is filled when payment is PRELEVEMENT
  checkFieldFilledIfPaymentIsPrelevement(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (TiersComponent.instanceOfTiers(this.tiers) && this.tiers.paymentType != undefined && this.tiers.paymentType.code != undefined && this.tiers.paymentType.code == PAYMENT_TYPE_PRELEVEMENT && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  // Check if the propertiy given in parameter is filled when refund is PRELEVEMENT
  checkFieldFilledIfRefundIsPrelevement(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (this.refundDocument != undefined && this.refundDocument.refundType != undefined && this.refundDocument.refundType.code != undefined && this.refundDocument.refundType.code == REFUND_TYPE_VIREMENT && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  compareWithId = compareWithId;

  getFormStatus(): boolean {
    this.settlementBillingForm.controls['paymentIBAN'].updateValueAndValidity();
    this.settlementBillingForm.controls['refundIBAN'].updateValueAndValidity();
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
}
