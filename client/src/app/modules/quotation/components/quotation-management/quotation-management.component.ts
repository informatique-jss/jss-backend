import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { Observable, Subscription } from 'rxjs';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { BILLING_TIERS_DOCUMENT_TYPE_CODE, BILLING_TIERS_DOCUMENT_TYPE_OTHER_CODE, COUNTRY_CODE_FRANCE, QUOTATION_DOCUMENT_TYPE_CODE, QUOTATION_TYPE_OTHER_CODE } from 'src/app/libs/Constants';
import { getDocument, replaceDocument } from 'src/app/libs/DocumentHelper';
import { instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { DocumentTypeService } from 'src/app/modules/miscellaneous/services/document.type.service';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { Document } from '../../../miscellaneous/model/Document';
import { DocumentType } from '../../../miscellaneous/model/DocumentType';
import { IQuotation } from '../../model/IQuotation';
import { QuotationLabelType } from '../../model/QuotationLabelType';
import { RecordType } from '../../model/RecordType';
import { QuotationLabelTypeService } from '../../services/quotation.label.type.service';
import { RecordTypeService } from '../../services/record.type.service';

@Component({
  selector: 'quotation-management',
  templateUrl: './quotation-management.component.html',
  styleUrls: ['./quotation-management.component.css']
})
export class QuotationManagementComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() quotation: IQuotation = {} as IQuotation;
  @Input() editMode: boolean = false;
  @Input() updateDocumentsEvent: Observable<void> | undefined;
  updateDocumentsSubscription: Subscription | undefined;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  documentTypes: DocumentType[] = [] as Array<DocumentType>;
  quotationLabelTypes: QuotationLabelType[] = [] as Array<QuotationLabelType>;
  recordTypes: RecordType[] = [] as Array<RecordType>;

  QUOTATION_TYPE_OTHER_CODE = QUOTATION_TYPE_OTHER_CODE;
  COUNTRY_CODE_FRANCE = COUNTRY_CODE_FRANCE;
  BILLING_TIERS_DOCUMENT_TYPE_OTHER_CODE = BILLING_TIERS_DOCUMENT_TYPE_OTHER_CODE;

  devisDocument: Document = {} as Document;
  billingDocument: Document = {} as Document;

  Validators = Validators;

  constructor(private formBuilder: UntypedFormBuilder,
    protected tiersService: TiersService,
    protected documentTypeService: DocumentTypeService,
    protected recordTypeService: RecordTypeService,
    protected cityService: CityService,
    protected quotationLabelTypeService: QuotationLabelTypeService) { }

  ngOnInit() {
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

  ngOnDestroy() {
    if (this.updateDocumentsSubscription)
      this.updateDocumentsSubscription.unsubscribe();
  }

  instanceOfQuotation = instanceOfQuotation;

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation != undefined) {
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
    this.documentTypeService.getDocumentTypes().subscribe(response => {
      this.documentTypes = response;
      if (this.quotation.tiers != null)
        this.tiersService.setCurrentViewedTiers(this.quotation.tiers)
      if (this.quotation.responsable != null && this.quotation.responsable.tiers != null) {
        this.tiersService.setCurrentViewedTiers(this.quotation.responsable.tiers)
        this.tiersService.setCurrentViewedResponsable(this.quotation.responsable);
      }
      this.devisDocument = getDocument(QUOTATION_DOCUMENT_TYPE_CODE, this.quotation, this.documentTypes);
      this.billingDocument = getDocument(BILLING_TIERS_DOCUMENT_TYPE_CODE, this.quotation, this.documentTypes);

      // If billing document does not exist, try to grab it from selected tiers, responsable or confrere
      if (!this.billingDocument.id) {
        if (this.quotation.responsable) {
          this.billingDocument = getDocument(BILLING_TIERS_DOCUMENT_TYPE_CODE, this.quotation.responsable, this.documentTypes);
        } else if (this.quotation.confrere) {
          this.billingDocument = getDocument(BILLING_TIERS_DOCUMENT_TYPE_CODE, this.quotation.confrere, this.documentTypes);
        } else if (this.quotation.tiers) {
          this.billingDocument = getDocument(BILLING_TIERS_DOCUMENT_TYPE_CODE, this.quotation.tiers, this.documentTypes);
        }
      }

      if (!this.billingDocument.billingLabelIsIndividual)
        this.billingDocument.billingLabelIsIndividual = false;

      if (this.billingDocument.id) {
        replaceDocument(BILLING_TIERS_DOCUMENT_TYPE_CODE, this.quotation, this.documentTypes, this.billingDocument);
      }
    })
  }

  getFormStatus(): boolean {
    this.quotationManagementForm.markAllAsTouched();
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
    if (this.billingDocument.billingLabelCountry == null || this.billingDocument.billingLabelCountry == undefined)
      this.billingDocument.billingLabelCountry = city.country;

    if (this.billingDocument.billingLabelCountry.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
      this.billingDocument.billingLabelPostalCode = city.postalCode;
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
