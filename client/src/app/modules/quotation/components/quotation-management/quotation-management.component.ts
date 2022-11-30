import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { Observable, Subscription } from 'rxjs';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { copyObject } from 'src/app/libs/GenericHelper';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { Document } from '../../../miscellaneous/model/Document';
import { DocumentType } from '../../../miscellaneous/model/DocumentType';
import { ITiers } from '../../../tiers/model/ITiers';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { IQuotation } from '../../model/IQuotation';
import { MailComputeResult } from '../../model/MailComputeResult';
import { QuotationLabelType } from '../../model/QuotationLabelType';
import { RecordType } from '../../model/RecordType';
import { InvoiceLabelResultService } from '../../services/invoice.label.result.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';
import { QuotationLabelTypeService } from '../../services/quotation.label.type.service';
import { RecordTypeService } from '../../services/record.type.service';

@Component({
  selector: 'quotation-management',
  templateUrl: './quotation-management.component.html',
  styleUrls: ['./quotation-management.component.css']
})
export class QuotationManagementComponent implements OnInit, AfterContentChecked {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() quotation: IQuotation = {} as IQuotation;
  @Input() editMode: boolean = false;
  @Input() isStatusOpen: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() updateDocumentsEvent: Observable<void> | undefined;
  updateDocumentsSubscription: Subscription | undefined;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  documentTypes: DocumentType[] = [] as Array<DocumentType>;
  quotationLabelTypes: QuotationLabelType[] = [] as Array<QuotationLabelType>;
  recordTypes: RecordType[] = [] as Array<RecordType>;

  countryFrance: Country = this.constantService.getCountryFrance();
  billingLabelTypeOther = this.constantService.getBillingLabelTypeOther();

  devisDocument: Document = {} as Document;
  billingDocument: Document = {} as Document;

  quotationLabelTypeOther: QuotationLabelType = this.constantService.getQuotationLabelTypeOther();

  quotationMailComputeResult: MailComputeResult | undefined;
  billingMailComputeResult: MailComputeResult | undefined;
  invoiceLabelResult: InvoiceLabelResult | undefined;

  Validators = Validators;

  constructor(private formBuilder: UntypedFormBuilder,
    protected tiersService: TiersService,
    protected recordTypeService: RecordTypeService,
    private constantService: ConstantService,
    protected cityService: CityService,
    private changeDetectorRef: ChangeDetectorRef,
    private invoiceLabelResultService: InvoiceLabelResultService,
    private mailComputeResultService: MailComputeResultService,
    protected quotationLabelTypeService: QuotationLabelTypeService) { }

  ngOnInit() {
    this.updateQuotationMailResult();
    this.updateBillingMailResult();
    this.updateInvoiceLabelResult();
    this.quotationManagementForm.markAllAsTouched();
    this.quotationLabelTypeService.getQuotationLabelTypes().subscribe(response => {
      this.quotationLabelTypes = response;
    })
    this.recordTypeService.getRecordTypes().subscribe(response => {
      this.recordTypes = response;
    })
    if (this.updateDocumentsEvent)
      this.updateDocumentsSubscription = this.updateDocumentsEvent.subscribe(() => this.setDocument());
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnDestroy() {
    if (this.updateDocumentsSubscription)
      this.updateDocumentsSubscription.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation != undefined) {
      this.updateQuotationMailResult();
      this.updateBillingMailResult();
      this.updateInvoiceLabelResult();
      if (this.quotation.recordType == null || this.quotation.recordType == undefined)
        this.quotation.recordType = this.recordTypes[0];
      if (this.quotation.quotationLabelType == null || this.quotation.quotationLabelType == undefined)
        this.quotation.quotationLabelType = this.quotationLabelTypes[0];
      this.setDocument();
      this.quotationManagementForm.markAllAsTouched();
    }
  }

  quotationManagementForm = this.formBuilder.group({
  });

  setDocument() {
    let currentOrderingCustomer: ITiers = {} as ITiers;
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
    this.devisDocument = getDocument(this.constantService.getDocumentTypeQuotation(), this.quotation);
    this.billingDocument = getDocument(this.constantService.getDocumentTypeBilling(), this.quotation);

    // If billing document does not exist, try to grab it from selected tiers, responsable or confrere
    if (!this.billingDocument.id)
      this.billingDocument = copyObject(getDocument(this.constantService.getDocumentTypeBilling(), currentOrderingCustomer));

    if (!this.billingDocument.billingLabelIsIndividual)
      this.billingDocument.billingLabelIsIndividual = false;
  }

  getFormStatus(): boolean {
    this.quotationManagementForm.markAllAsTouched();
    this.updateQuotationMailResult();
    this.updateBillingMailResult();
    this.updateInvoiceLabelResult();
    if (!this.isStatusOpen && (!this.quotationMailComputeResult?.recipientsMailTo || this.quotationMailComputeResult?.recipientsMailTo.length == 0))
      return false;
    if (!this.isStatusOpen && instanceOfCustomerOrder(this.quotation) && (!this.invoiceLabelResult?.billingLabel || !this.invoiceLabelResult.billingLabelAddress || !this.invoiceLabelResult.billingLabelCity
      || !this.invoiceLabelResult.billingLabelCountry || !this.invoiceLabelResult.billingLabelPostalCode))
      return false;
    return this.quotationManagementForm.valid;
  }

  fillTiers(tiers: Tiers) {
    this.quotation.customLabelTiers = tiers;
    this.quotation.customLabelResponsable = undefined;
  }

  fillResponsable(responsable: Responsable) {
    this.quotation.customLabelResponsable = responsable;
    this.quotation.customLabelTiers = undefined;
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

  updateQuotationMailResult() {
    if (this.quotation && this.quotation.id)
      this.mailComputeResultService.getMailComputeResultForQuotation(this.quotation).subscribe(response => {
        this.quotationMailComputeResult = response;
      })
  }

  updateBillingMailResult() {
    if (this.quotation && this.quotation.id)
      this.mailComputeResultService.getMailComputeResultForBilling(this.quotation).subscribe(response => {
        this.billingMailComputeResult = response;
      })
  }

  updateInvoiceLabelResult() {
    if (this.quotation && this.quotation.id && instanceOfCustomerOrder(this.quotation))
      this.invoiceLabelResultService.getInvoiceLabelComputeResult(this.quotation).subscribe(response => {
        this.invoiceLabelResult = response;
      });
  }
}
