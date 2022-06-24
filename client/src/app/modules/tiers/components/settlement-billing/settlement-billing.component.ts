import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { BILLING_CLOSURE_RECIPIENT_OTHERS_CODE, BILLING_CLOSURE_TIERS_DOCUMENT_TYPE_CODE, BILLING_TIERS_DOCUMENT_TYPE_CODE, BILLING_TIERS_DOCUMENT_TYPE_OTHER_CODE, CFE_TIERS_DOCUMENT_TYPE_CODE, DUNNING_TIERS_DOCUMENT_TYPE_CODE, KBIS_TIERS_DOCUMENT_TYPE_CODE, PAYMENT_TYPE_CHEQUES, PAYMENT_TYPE_OTHERS, PAYMENT_TYPE_PRELEVEMENT, PROVISIONAL_RECEIPT_TIERS_DOCUMENT_TYPE_CODE, PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, REFUND_TIERS_DOCUMENT_TYPE_CODE, REFUND_TYPE_VIREMENT } from 'src/app/libs/Constants';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { instanceOfResponsable, instanceOfTiers } from 'src/app/libs/TypeHelper';
import { PaymentType } from 'src/app/modules/miscellaneous/model/PaymentType';
import { DocumentTypeService } from 'src/app/modules/miscellaneous/services/document.type.service';
import { PaymentTypeService } from 'src/app/modules/miscellaneous/services/payment-type.service';
import { Document } from "../../../miscellaneous/model/Document";
import { DocumentType } from "../../../miscellaneous/model/DocumentType";
import { ITiers } from '../../model/ITiers';
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
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;
  paymentTypes: PaymentType[] = [] as Array<PaymentType>;
  PAYMENT_TYPE_PRELEVEMENT = PAYMENT_TYPE_PRELEVEMENT;
  PAYMENT_TYPE_CHEQUES = PAYMENT_TYPE_CHEQUES;
  PAYMENT_TYPE_OTHERS = PAYMENT_TYPE_OTHERS;

  REFUND_TYPE_VIREMENT = REFUND_TYPE_VIREMENT;
  BILLING_TIERS_DOCUMENT_TYPE_OTHER_CODE = BILLING_TIERS_DOCUMENT_TYPE_OTHER_CODE;
  BILLING_CLOSURE_RECIPIENT_OTHERS_CODE = BILLING_CLOSURE_RECIPIENT_OTHERS_CODE;

  documentTypes: DocumentType[] = [] as Array<DocumentType>;
  billingDocument: Document = {} as Document;
  dunningDocument: Document = {} as Document;
  refundDocument: Document = {} as Document;
  provisionalReceiptDocument: Document = {} as Document;
  billingClosureDocument: Document = {} as Document;
  publicationDocument: Document = {} as Document;
  cfeDocument: Document = {} as Document;
  kbisDocument: Document = {} as Document;

  constructor(private formBuilder: UntypedFormBuilder,
    protected paymentTypeService: PaymentTypeService,
    protected documentTypeService: DocumentTypeService,
    protected tiersService: TiersService,
  ) { }

  ngOnInit() {
    // Referential loading
    this.paymentTypeService.getPaymentTypes().subscribe(response => {
      this.paymentTypes = response;
    })

    // Trigger it to show mandatory fields
    this.settlementBillingForm.markAllAsTouched();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined) {
      if (instanceOfTiers(this.tiers)) {
        if ((this.tiers.paymentType == null || this.tiers.paymentType == undefined) && this.paymentTypes.length > 0) {
          for (const paymentType of this.paymentTypes) {
            if (paymentType.code == PAYMENT_TYPE_PRELEVEMENT)
              this.tiers.paymentType = paymentType;
          }
        }
      }

      this.documentTypeService.getDocumentTypes().subscribe(response => {
        this.documentTypes = response;

        this.billingDocument = getDocument(BILLING_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.documentTypes);
        this.billingClosureDocument = getDocument(BILLING_CLOSURE_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.documentTypes);
        if (this.billingClosureDocument.isRecipientClient == null || this.billingClosureDocument.isRecipientClient == false)
          this.billingClosureDocument.isRecipientClient = true;

        if (instanceOfTiers(this.tiers)) {
          this.dunningDocument = getDocument(DUNNING_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.documentTypes);
          this.refundDocument = getDocument(REFUND_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.documentTypes);
          this.provisionalReceiptDocument = getDocument(PROVISIONAL_RECEIPT_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.documentTypes);
          this.publicationDocument = getDocument(PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.documentTypes);
          this.cfeDocument = getDocument(CFE_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.documentTypes);
          this.kbisDocument = getDocument(KBIS_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.documentTypes);
        }

      })
    }
    this.settlementBillingForm.markAllAsTouched();
  }

  Validators = Validators;

  settlementBillingForm = this.formBuilder.group({
    paymentType: [''],
    paymentDeadlineType: [''],
    billingClosureRecipientType: [''],
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
}
