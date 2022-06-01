import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { DocumentTypeService } from 'src/app/modules/miscellaneous/services/document.type.service';
import { IQuotation } from '../../model/IQuotation';
import { Document } from '../../../miscellaneous/model/Document';
import { DocumentType } from '../../../miscellaneous/model/DocumentType';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { QUOTATION_DOCUMENT_TYPE_CODE, QUOTATION_TYPE_OTHER_CODE } from 'src/app/libs/Constants';
import { QuotationLabelType } from '../../model/QuotationLabelType';
import { QuotationLabelTypeService } from '../../services/quotation.label.type.service';
import { RecordType } from '../../model/RecordType';
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

  documentTypes: DocumentType[] = [] as Array<DocumentType>;
  quotationLabelTypes: QuotationLabelType[] = [] as Array<QuotationLabelType>;
  recordTypes: RecordType[] = [] as Array<RecordType>;

  QUOTATION_TYPE_OTHER_CODE = QUOTATION_TYPE_OTHER_CODE;

  devisDocument: Document = {} as Document;

  constructor(private formBuilder: FormBuilder,
    protected tiersService: TiersService,
    protected documentTypeService: DocumentTypeService,
    protected recordTypeService: RecordTypeService,
    protected quotationLabelTypeService: QuotationLabelTypeService) { }

  ngOnInit() {
    this.quotationManagementForm.markAllAsTouched();
    this.quotationLabelTypeService.getQuotationLabelTypes().subscribe(response => {
      this.quotationLabelTypes = response;
    })
    this.recordTypeService.getRecordTypes().subscribe(response => {
      this.recordTypes = response;
    })
  }

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
    quotationLabelType: ['', []],
    recordType: ['', []],
    quotationLabel: ['', Validators.maxLength(40)],
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
    })

  }

}
