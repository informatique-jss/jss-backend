import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { CFE_TIERS_DOCUMENT_TYPE_CODE, KBIS_TIERS_DOCUMENT_TYPE_CODE, PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { ITiers } from '../../model/ITiers';
import { TiersDocumentType } from '../../model/TiersDocumentType';
import { TiersDocumentTypeService } from '../../services/tiers.document.type.service';
import { TiersComponent } from '../tiers/tiers.component';
import { TiersDocument } from './../../model/TiersDocument';

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

  tiersDocumentTypes: TiersDocumentType[] = [] as Array<TiersDocumentType>;

  publicationDocument: TiersDocument = {} as TiersDocument;
  cfeDocument: TiersDocument = {} as TiersDocument;
  kbisDocument: TiersDocument = {} as TiersDocument;

  constructor(private formBuilder: FormBuilder,
    private tiersDocumentTypeService: TiersDocumentTypeService) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined) {
      this.tiersDocumentTypeService.getDocumentTypes().subscribe(response => {
        this.tiersDocumentTypes = response;
        this.publicationDocument = TiersComponent.getDocument(PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
        this.cfeDocument = TiersComponent.getDocument(CFE_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
        this.kbisDocument = TiersComponent.getDocument(KBIS_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
      })
      this.documentManagementForm.markAllAsTouched();
    }
  }

  ngOnInit() {
  }

  instanceOfResponsable = TiersComponent.instanceOfResponsable;

  documentManagementForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.documentManagementForm.valid;
  }
}
