import { AfterContentChecked, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { Observable, Subscription } from 'rxjs';
import { getDocument, replaceDocument } from 'src/app/libs/DocumentHelper';
import { copyObject } from 'src/app/libs/GenericHelper';
import { instanceOfCustomerOrder, instanceOfResponsable } from 'src/app/libs/TypeHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { getCustomerOrderForIQuotation } from '../../../invoicing/components/invoice-tools';
import { Document } from '../../../miscellaneous/model/Document';
import { DocumentType } from '../../../miscellaneous/model/DocumentType';
import { ITiers } from '../../../tiers/model/ITiers';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { IQuotation } from '../../model/IQuotation';
import { MailComputeResult } from '../../model/MailComputeResult';
import { RecordType } from '../../model/RecordType';
import { InvoiceLabelResultService } from '../../services/invoice.label.result.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';
import { RecordTypeService } from '../../services/record.type.service';

@Component({
  selector: 'quotation-management',
  templateUrl: './quotation-management.component.html',
  styleUrls: ['./quotation-management.component.css']
})
export class QuotationManagementComponent implements OnInit, AfterContentChecked {


  @Input() quotation: IQuotation = {} as IQuotation;
  @Output() quotationChange: EventEmitter<IQuotation> = new EventEmitter<IQuotation>();
  @Input() editMode: boolean = false;
  @Input() isStatusOpen: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() updateDocumentsEvent: Observable<IQuotation> | undefined;
  updateDocumentsSubscription: Subscription | undefined;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  documentTypes: DocumentType[] = [] as Array<DocumentType>;
  recordTypes: RecordType[] = [] as Array<RecordType>;

  countryFrance: Country = this.constantService.getCountryFrance();
  billingLabelTypeOther = this.constantService.getBillingLabelTypeOther();
  billingDocument: Document = {} as Document;
  digitalDocument: Document = {} as Document;
  paperDocument: Document = {} as Document;

  instanceOfResponsable = instanceOfResponsable;
  getCustomerOrderForIQuotation = getCustomerOrderForIQuotation;

  billingMailComputeResult: MailComputeResult | undefined;
  digitalMailComputeResult: MailComputeResult | undefined;
  invoiceLabelResult: InvoiceLabelResult | undefined;
  paperLabelResult: InvoiceLabelResult | undefined;

  Validators = Validators;

  constructor(private formBuilder: UntypedFormBuilder,
    protected tiersService: TiersService,
    protected recordTypeService: RecordTypeService,
    private constantService: ConstantService,
    protected cityService: CityService,
    private changeDetectorRef: ChangeDetectorRef,
    private invoiceLabelResultService: InvoiceLabelResultService,
    private mailComputeResultService: MailComputeResultService) { }

  ngOnInit() {
    this.updateBillingMailResult();
    this.updateInvoiceLabelResult();
    this.updatePaperLabelResult();
    this.quotationManagementForm.markAllAsTouched();
    this.recordTypeService.getRecordTypes().subscribe(response => {
      this.recordTypes = response;
    })
    if (this.updateDocumentsEvent)
      this.updateDocumentsSubscription = this.updateDocumentsEvent.subscribe((quotation) => {
        this.setDocument();
        this.updateBillingMailResult();
        this.updateInvoiceLabelResult();
        this.updatePaperLabelResult();
      }
      );
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnDestroy() {
    if (this.updateDocumentsSubscription)
      this.updateDocumentsSubscription.unsubscribe();
  }

  updateAdressing(event: any) {
    this.updateBillingMailResult();
    this.updateInvoiceLabelResult();
    this.updatePaperLabelResult();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation != undefined) {
      this.setDocument();
      this.quotationManagementForm.markAllAsTouched();
    }
  }

  quotationManagementForm = this.formBuilder.group({
  });

  setDocument() {
    let currentOrderingCustomer: ITiers | undefined;
    if (this.quotation.tiers) {
      this.tiersService.setCurrentViewedTiers(this.quotation.tiers)
      currentOrderingCustomer = this.quotation.tiers;
    }
    if (this.quotation.responsable && this.quotation.responsable.tiers) {
      this.tiersService.setCurrentViewedTiers(this.quotation.responsable.tiers)
      this.tiersService.setCurrentViewedResponsable(this.quotation.responsable);
      currentOrderingCustomer = this.quotation.responsable;
    }
    if (this.quotation.confrere)
      currentOrderingCustomer = this.quotation.confrere;

    if (!currentOrderingCustomer)
      return;

    this.billingDocument = getDocument(this.constantService.getDocumentTypeBilling(), this.quotation);
    let currentBillingDocument = getDocument(this.constantService.getDocumentTypeBilling(), currentOrderingCustomer);

    // If billing document does not exist, try to grab it from selected tiers, responsable or confrere
    if (!this.billingDocument.id && currentBillingDocument.id) {
      this.billingDocument = copyObject(getDocument(this.constantService.getDocumentTypeBilling(), currentOrderingCustomer));
      if (!this.billingDocument.billingLabelIsIndividual)
        this.billingDocument.billingLabelIsIndividual = false;
    }

    this.digitalDocument = getDocument(this.constantService.getDocumentTypeDigital(), this.quotation);
    let currentDigitalDocument = getDocument(this.constantService.getDocumentTypeDigital(), currentOrderingCustomer);

    // If digital document does not exist, try to grab it from selected tiers, responsable or confrere
    if (!this.digitalDocument.id && currentDigitalDocument.id) {
      this.digitalDocument = copyObject(getDocument(this.constantService.getDocumentTypeDigital(), currentOrderingCustomer));
      if (!this.digitalDocument.billingLabelIsIndividual)
        this.digitalDocument.billingLabelIsIndividual = false;
    }

    this.paperDocument = getDocument(this.constantService.getDocumentTypePaper(), this.quotation);
    let currentPaperDocument = getDocument(this.constantService.getDocumentTypePaper(), currentOrderingCustomer);

    // If digital document does not exist, try to grab it from selected tiers, responsable or confrere
    if (!this.paperDocument.id && currentPaperDocument.id) {
      this.paperDocument = copyObject(getDocument(this.constantService.getDocumentTypePaper(), currentOrderingCustomer));
      if (!this.paperDocument.billingLabelIsIndividual)
        this.paperDocument.billingLabelIsIndividual = false;
    }
  }

  getBillingDocument() {
    return this.billingDocument;
  }

  getDigitalDocument() {
    return this.digitalDocument;
  }

  getPaperDocument() {
    return this.paperDocument;
  }

  getFormStatus(): boolean {
    this.quotationManagementForm.markAllAsTouched();
    this.updateBillingMailResult();
    this.updateInvoiceLabelResult();
    this.updatePaperLabelResult();
    if (!this.isStatusOpen && instanceOfCustomerOrder(this.quotation) && (!this.invoiceLabelResult?.billingLabel || !this.invoiceLabelResult.billingLabelAddress || !this.invoiceLabelResult.billingLabelCity
      || !this.invoiceLabelResult.billingLabelCountry || !this.invoiceLabelResult.billingLabelPostalCode))
      return false;
    if (!this.isStatusOpen && (!this.billingMailComputeResult || !this.billingMailComputeResult.recipientsMailTo || this.billingMailComputeResult.recipientsMailTo.length == 0
      || !this.digitalMailComputeResult || !this.digitalMailComputeResult.recipientsMailTo || this.digitalMailComputeResult.recipientsMailTo.length == 0))
      return false;
    return this.quotationManagementForm.valid;
  }

  fillPostalCode(city: City) {
    if (!this.billingDocument.billingLabelCountry)
      this.billingDocument.billingLabelCountry = city.country;

    if (this.billingDocument.billingLabelCountry.id == this.countryFrance.id && city.postalCode != null && !this.billingDocument.billingPostalCode)
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

  isLoadingMailResult: boolean = false;
  isLoadingInvoiceLabelResult: boolean = false;
  isLoadingPaperLabelResult: boolean = false;

  updateBillingMailResult() {
    if (!this.isLoadingMailResult && this.quotation && (this.quotation.tiers || this.quotation.confrere || this.quotation.responsable)) {
      this.isLoadingMailResult = true;
      // Can't find a way to make it work correctly ...
      replaceDocument(this.constantService.getDocumentTypeBilling(), this.quotation, this.billingDocument);
      replaceDocument(this.constantService.getDocumentTypeDigital(), this.quotation, this.digitalDocument);
      replaceDocument(this.constantService.getDocumentTypePaper(), this.quotation, this.paperDocument);

      this.mailComputeResultService.getMailComputeResultForBilling(this.quotation).subscribe(response => {
        this.isLoadingMailResult = false;
        this.billingMailComputeResult = response;
      })

      this.mailComputeResultService.getMailComputeResultForDigital(this.quotation).subscribe(response => {
        this.digitalMailComputeResult = response;
      })
    }
  }

  updateInvoiceLabelResult() {
    if (!this.isLoadingInvoiceLabelResult && this.quotation && this.quotation.id && instanceOfCustomerOrder(this.quotation) && (this.quotation.tiers || this.quotation.confrere || this.quotation.responsable)) {
      this.isLoadingInvoiceLabelResult = true;
      this.invoiceLabelResultService.getInvoiceLabelComputeResult(this.quotation).subscribe(response => {
        this.isLoadingInvoiceLabelResult = false;
        if (response && response.billingLabel)
          this.invoiceLabelResult = response;
      });
    }
  }

  updatePaperLabelResult() {
    if (!this.isLoadingPaperLabelResult && this.quotation && this.quotation.id && (this.quotation.tiers || this.quotation.confrere || this.quotation.responsable)) {
      this.isLoadingPaperLabelResult = true;
      this.invoiceLabelResultService.getPaperLabelComputeResult(this.quotation).subscribe(response => {
        this.isLoadingPaperLabelResult = false;
        if (response && response.billingLabel)
          this.paperLabelResult = response;
      });
    }
  }
}
