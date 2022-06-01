import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { CFE_TIERS_DOCUMENT_TYPE_CODE, KBIS_TIERS_DOCUMENT_TYPE_CODE, PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { DocumentTypeService } from 'src/app/modules/miscellaneous/services/document.type.service';
import { ITiers } from '../../model/ITiers';
import { TiersComponent } from '../tiers/tiers.component';
import { Document } from "../../../miscellaneous/model/Document";
import { DocumentType } from "../../../miscellaneous/model/DocumentType";
import { instanceOfResponsable } from 'src/app/libs/TypeHelper';
import { getDocument } from 'src/app/libs/DocumentHelper';

@Component({
  selector: 'document-management',
  templateUrl: './document-management.component.html',
  styleUrls: ['./document-management.component.css']
})
export class DocumentManagementComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();

  @Input() tiers: ITiers = {} as ITiers;
  @Input() editMode: boolean = false;

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;

  documentTypes: DocumentType[] = [] as Array<DocumentType>;

  publicationDocument: Document = {} as Document;
  cfeDocument: Document = {} as Document;
  kbisDocument: Document = {} as Document;

  constructor(private formBuilder: FormBuilder,
    private documentTypeService: DocumentTypeService) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined) {
      this.documentTypeService.getDocumentTypes().subscribe(response => {
        this.documentTypes = response;
        this.publicationDocument = getDocument(PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.documentTypes);
        this.cfeDocument = getDocument(CFE_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.documentTypes);
        this.kbisDocument = getDocument(KBIS_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.documentTypes);
      })
      this.documentManagementForm.markAllAsTouched();
    }
  }

  ngOnInit() {
  }

  instanceOfResponsable = instanceOfResponsable;

  documentManagementForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.documentManagementForm.valid;
  }
}
