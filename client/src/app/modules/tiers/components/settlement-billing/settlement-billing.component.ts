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
import { Mail } from '../../model/Mail';
import { PaymentDeadlineType } from '../../model/PaymentDeadlineType';
import { RefundType } from '../../model/RefundType';
import { Tiers } from '../../model/Tiers';
import { TiersDocument } from '../../model/TiersDocument';
import { TiersDocumentType } from '../../model/TiersDocumentType';
import { BillingClosureRecipientTypeService } from '../../services/billing.closure.recipient.type.service';
import { BillingClosureTypeService } from '../../services/billing.closure.type.service';
import { BillingLabelTypeService } from '../../services/billing.label.type.service';
import { PaymentDeadlineTypeService } from '../../services/payment.deadline.type.service';
import { RefundTypeService } from '../../services/refund.type.service';
import { TiersComponent } from '../tiers/tiers.component';
import { TiersDocumentTypeService } from './../../services/tiers.document.type.service';

@Component({
  selector: 'settlement-billing',
  templateUrl: './settlement-billing.component.html',
  styleUrls: ['./settlement-billing.component.css']
})
export class SettlementBillingComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() tiers: Tiers = {} as Tiers;
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
      if ((this.tiers.paymentType == null || this.tiers.paymentType != undefined) && this.paymentTypes.length > 0) {
        this.paymentTypes.forEach(paymentType => {
          if (paymentType.code == PAYMENT_TYPE_PRELEVEMENT)
            this.tiers.paymentType = paymentType;
        })
      }

      if (this.tiers.isProvisionalPaymentMandatory == undefined || this.tiers.isProvisionalPaymentMandatory == null)
        this.tiers.isProvisionalPaymentMandatory = false;

      this.tiersDocumentTypeService.getDocumentTypes().subscribe(response => {
        this.tiersDocumentTypes = response;
        this.billingDocument = TiersComponent.getDocument(BILLING_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);

        if (this.billingDocument.isResponsableOnBilling == undefined || this.billingDocument.isResponsableOnBilling == null)
          this.billingDocument.isResponsableOnBilling = false;

        if (this.billingDocument.isCommandNumberMandatory == undefined || this.billingDocument.isCommandNumberMandatory == null)
          this.billingDocument.isCommandNumberMandatory = false;

        if (this.billingDocument.affaireAddress != null && this.billingDocument.affaireAddress != undefined && this.billingDocument.affaireAddress != "")
          this.overrideBillingAffaireAddress = true;
        if (this.billingDocument.affaireRecipient != null && this.billingDocument.affaireRecipient != undefined && this.billingDocument.affaireRecipient != "")
          this.overrideBillingAffaireAddress = true;
        if (this.billingDocument.clientAddress != null && this.billingDocument.clientAddress != undefined && this.billingDocument.clientAddress != "")
          this.overrideBillingClientAddress = true;
        if (this.billingDocument.clientRecipient != null && this.billingDocument.clientRecipient != undefined && this.billingDocument.clientRecipient != "")
          this.overrideBillingClientAddress = true;

        if (this.billingDocument.mailsAffaire != null && this.billingDocument.mailsAffaire != undefined && this.billingDocument.mailsAffaire.length > 0)
          this.overrideBillingAffaireMail = true;
        if (this.billingDocument.mailsClient != null && this.billingDocument.mailsClient != undefined && this.billingDocument.mailsClient.length > 0)
          this.overrideBillingClientMail = true;

        this.dunningDocument = TiersComponent.getDocument(DUNNING_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
        if (this.dunningDocument.isAutomaticDunning == undefined || this.billingDocument.isAutomaticDunning == null)
          this.dunningDocument.isAutomaticDunning = false;

        if (this.dunningDocument.affaireAddress != null && this.dunningDocument.affaireAddress != undefined && this.dunningDocument.affaireAddress != "")
          this.overrideDunningAffaireAddress = true;
        if (this.dunningDocument.affaireRecipient != null && this.dunningDocument.affaireRecipient != undefined && this.dunningDocument.affaireRecipient != "")
          this.overrideDunningAffaireAddress = true;
        if (this.dunningDocument.clientAddress != null && this.dunningDocument.clientAddress != undefined && this.dunningDocument.clientAddress != "")
          this.overrideDunningClientAddress = true;
        if (this.dunningDocument.clientRecipient != null && this.dunningDocument.clientRecipient != undefined && this.dunningDocument.clientRecipient != "")
          this.overrideDunningClientAddress = true;

        if (this.dunningDocument.mailsAffaire != null && this.dunningDocument.mailsAffaire != undefined && this.dunningDocument.mailsAffaire.length > 0)
          this.overrideDunningAffaireMail = true;
        if (this.dunningDocument.mailsClient != null && this.dunningDocument.mailsClient != undefined && this.dunningDocument.mailsClient.length > 0)
          this.overrideDunningClientMail = true;

        this.refundDocument = TiersComponent.getDocument(REFUND_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
        if (this.refundDocument.isRefundable == undefined || this.refundDocument.isRefundable == null)
          this.refundDocument.isRefundable = false;

        if (this.refundDocument.affaireAddress != null && this.refundDocument.affaireAddress != undefined && this.refundDocument.affaireAddress != "")
          this.overrideRefundAffaireAddress = true;
        if (this.refundDocument.affaireRecipient != null && this.refundDocument.affaireRecipient != undefined && this.refundDocument.affaireRecipient != "")
          this.overrideRefundAffaireAddress = true;
        if (this.refundDocument.clientAddress != null && this.refundDocument.clientAddress != undefined && this.refundDocument.clientAddress != "")
          this.overrideRefundClientAddress = true;
        if (this.refundDocument.clientRecipient != null && this.refundDocument.clientRecipient != undefined && this.refundDocument.clientRecipient != "")
          this.overrideRefundClientAddress = true;

        if (this.refundDocument.mailsAffaire != null && this.refundDocument.mailsAffaire != undefined && this.refundDocument.mailsAffaire.length > 0)
          this.overrideRefundAffaireMail = true;
        if (this.refundDocument.mailsClient != null && this.refundDocument.mailsClient != undefined && this.refundDocument.mailsClient.length > 0)
          this.overrideRefundClientMail = true;

        this.billingClosureDocument = TiersComponent.getDocument(BILLING_CLOSURE_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
        this.billingClosureDocument.isRecipientClient = true;
        this.billingClosureDocument.isRecipientAffaire = false;

        if (this.billingClosureDocument.clientAddress != null && this.billingClosureDocument.clientAddress != undefined && this.billingClosureDocument.clientAddress != "")
          this.overrideBillingClosureClientAddress = true;
        if (this.billingClosureDocument.clientRecipient != null && this.billingClosureDocument.clientRecipient != undefined && this.billingClosureDocument.clientRecipient != "")
          this.overrideBillingClosureClientAddress = true;

        if (this.billingClosureDocument.mailsClient != null && this.billingClosureDocument.mailsClient != undefined && this.billingClosureDocument.mailsClient.length > 0)
          this.overrideBillingClosureClientMail = true;

        this.provisionalReceiptDocument = TiersComponent.getDocument(PROVISIONAL_RECEIPT_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
        this.provisionalReceiptDocument.isMailingPdf = true;
        this.provisionalReceiptDocument.isMailingPaper = false;

        if (this.provisionalReceiptDocument.mailsAffaire != null && this.provisionalReceiptDocument.mailsAffaire != undefined && this.provisionalReceiptDocument.mailsAffaire.length > 0)
          this.overrideProvisionalReceiptAffaireMail = true;
        if (this.provisionalReceiptDocument.mailsClient != null && this.provisionalReceiptDocument.mailsClient != undefined && this.provisionalReceiptDocument.mailsClient.length > 0)
          this.overrideProvisionalReceiptClientMail = true;

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
    billingRecipientTypeAffaire: [''],
    billingRecipientTypeClient: [''],
    billingMailingTypePaper: [''],
    billingMailingTypePdf: [''],
    billingAffaireRecipient: [''],
    billingClientRecipient: [''],
    billingClientMails: [''],
    billingAffaireAddress: [''],
    billingClientAddress: [''],
    billingMailsClient: [''],
    billingMailsAffaire: [''],
    isAutomaticDunning: [''],
    dunningRecipientTypeAffaire: [''],
    dunningRecipientTypeClient: [''],
    dunningMailingTypePaper: [''],
    dunningMailingTypePdf: [''],
    dunningAffaireRecipient: [''],
    dunningClientRecipient: [''],
    dunningClientMails: [''],
    dunningAffaireAddress: [''],
    dunningClientAddress: [''],
    dunningMailsClient: [''],
    dunningMailsAffaire: [''],
    paymentDeadlineType: [''],
    refundRecipientTypeAffaire: [''],
    refundRecipientTypeClient: [''],
    refundMailingTypePaper: [''],
    refundMailingTypePdf: [''],
    refundAffaireRecipient: [''],
    refundClientRecipient: [''],
    refundClientMails: [''],
    refundAffaireAddress: [''],
    refundClientAddress: [''],
    refundMailsClient: [''],
    refundMailsAffaire: [''],
    refundIBAN: ['', [this.checkFieldFilledIfRefundIsPrelevement("refundIBAN"), Validators.maxLength(40)]],
    isRefundable: [''],
    refundType: [''],
    billingClosureRecipientTypeClient: [''],
    billingClosureMailingTypePaper: [''],
    billingClosureMailingTypePdf: [''],
    billingClosureClientRecipient: [''],
    billingClosureClientMails: [''],
    billingClosureClientAddress: [''],
    billingClosureMailsClient: [''],
    billingClosureType: [''],
    billingClosureRecipientType: [''],
    provisionalReceiptRecipientTypeAffaire: [''],
    provisionalReceiptRecipientTypeClient: [''],
    provisionalReceiptMailsClient: [''],
    provisionalReceiptMailsAffaire: [''],
  });

  // Check if the propertiy given in parameter is filled when payment is PRELEVEMENT
  checkFieldFilledIfPaymentIsPrelevement(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (this.tiers.paymentType != undefined && this.tiers.paymentType.code != undefined && this.tiers.paymentType.code == PAYMENT_TYPE_PRELEVEMENT && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
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

  addMailClient(event: MatChipInputEvent, document: TiersDocument, formFieldName: string): void {
    const value = (event.value || '').trim();
    let mail: Mail = {} as Mail;
    if (value && validateEmail(value)) {
      mail.mail = value;
      if (document.mailsClient == undefined || document.mailsClient == null)
        document.mailsClient = [] as Mail[];
      document.mailsClient.push(mail);
    }
    event.chipInput!.clear();
    this.settlementBillingForm.get(formFieldName)?.setValue(null);
  }

  removeMailClient(inputMail: Mail, document: TiersDocument): void {
    if (document.mailsClient != undefined && document.mailsClient != null && this.editMode)
      for (let i = 0; i < document.mailsClient.length; i++) {
        const mail = document.mailsClient[i];
        if (mail.mail == inputMail.mail) {
          document.mailsClient.splice(i, 1);
          return;
        }
      }
  }

  removeAllMailClient(document: TiersDocument): void {
    document.mailsClient = [] as Array<Mail>;
  }

  addMailAffaire(event: MatChipInputEvent, document: TiersDocument, formFieldName: string): void {
    const value = (event.value || '').trim();
    let mail: Mail = {} as Mail;
    if (value && validateEmail(value)) {
      mail.mail = value;
      if (document.mailsAffaire == undefined || document.mailsAffaire == null)
        document.mailsAffaire = [] as Mail[];
      document.mailsAffaire.push(mail);
    }
    event.chipInput!.clear();
    this.settlementBillingForm.get(formFieldName)?.setValue(null);
  }

  removeMailAffaire(inputMail: Mail, document: TiersDocument): void {
    if (document.mailsAffaire != undefined && document.mailsAffaire != null && this.editMode)
      for (let i = 0; i < document.mailsAffaire.length; i++) {
        const mail = document.mailsAffaire[i];
        if (mail.mail == inputMail.mail) {
          document.mailsAffaire.splice(i, 1);
          return;
        }
      }
  }

  removeAllMailAffaire(document: TiersDocument): void {
    document.mailsAffaire = [] as Array<Mail>;
  }

  prepareMail = function (mail: Mail) {
    prepareMail(mail.mail, null, null);
  }

  compareWithId = compareWithId;

  getFormStatus(): boolean {
    this.settlementBillingForm.controls['paymentIBAN'].updateValueAndValidity();
    this.settlementBillingForm.controls['refundIBAN'].updateValueAndValidity();
    this.settlementBillingForm.markAllAsTouched();
    return this.settlementBillingForm.valid;
  }

}
