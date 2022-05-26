import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { Observable, Subscription } from 'rxjs';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { CFE_TIERS_DOCUMENT_TYPE_CODE, KBIS_TIERS_DOCUMENT_TYPE_CODE, PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { validateEmail } from 'src/app/libs/CustomFormsValidatorsHelper';
import { prepareMail } from 'src/app/libs/MailHelper';
import { Mail } from '../../model/Mail';
import { Tiers } from '../../model/Tiers';
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

  @Input() tiers: Tiers = {} as Tiers;
  @Input() editMode: boolean = false;

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;

  overridePublicationAffaireAddress: boolean = false;
  overridePublicationAffaireMail: boolean = false;
  overridePublicationClientAddress: boolean = false;
  overridePublicationClientMail: boolean = false;

  overrideCfeAffaireAddress: boolean = false;
  overrideCfeAffaireMail: boolean = false;
  overrideCfeClientAddress: boolean = false;
  overrideCfeClientMail: boolean = false;

  overrideKbisAffaireAddress: boolean = false;
  overrideKbisAffaireMail: boolean = false;
  overrideKbisClientAddress: boolean = false;
  overrideKbisClientMail: boolean = false;

  tiersDocumentTypes: TiersDocumentType[] = [] as Array<TiersDocumentType>;

  publicationDocument: TiersDocument = {} as TiersDocument;
  cfeDocument: TiersDocument = {} as TiersDocument;
  kbisDocument: TiersDocument = {} as TiersDocument;

  constructor(private formBuilder: FormBuilder,
    private tiersDocumentTypeService: TiersDocumentTypeService) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined) {
      if (this.tiers.documents != null && this.tiers.documents != undefined && this.tiers.documents.length > 0) {
        this.tiers.documents.forEach(document => {
          if (document.isMailingPaper == undefined || document.isMailingPaper == null)
            document.isMailingPaper = false;
          if (document.isMailingPdf == undefined || document.isMailingPdf == null)
            document.isMailingPdf = false;
          if (document.isRecipientAffaire == undefined || document.isRecipientAffaire == null)
            document.isRecipientAffaire = false;
          if (document.isRecipientClient == undefined || document.isRecipientClient == null)
            document.isRecipientClient = false;
        })
      }
      this.tiersDocumentTypeService.getDocumentTypes().subscribe(response => {
        this.tiersDocumentTypes = response;
        this.publicationDocument = TiersComponent.getDocument(PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
        this.cfeDocument = TiersComponent.getDocument(CFE_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);
        this.kbisDocument = TiersComponent.getDocument(KBIS_TIERS_DOCUMENT_TYPE_CODE, this.tiers, this.tiersDocumentTypes);

        if (this.publicationDocument.affaireAddress != null && this.publicationDocument.affaireAddress != undefined && this.publicationDocument.affaireAddress != "")
          this.overridePublicationAffaireAddress = true;
        if (this.publicationDocument.affaireRecipient != null && this.publicationDocument.affaireRecipient != undefined && this.publicationDocument.affaireRecipient != "")
          this.overridePublicationAffaireAddress = true;
        if (this.publicationDocument.clientAddress != null && this.publicationDocument.clientAddress != undefined && this.publicationDocument.clientAddress != "")
          this.overridePublicationClientAddress = true;
        if (this.publicationDocument.clientRecipient != null && this.publicationDocument.clientRecipient != undefined && this.publicationDocument.clientRecipient != "")
          this.overridePublicationClientAddress = true;

        if (this.publicationDocument.mailsAffaire != null && this.publicationDocument.mailsAffaire != undefined && this.publicationDocument.mailsAffaire.length > 0)
          this.overridePublicationAffaireMail = true;
        if (this.publicationDocument.mailsClient != null && this.publicationDocument.mailsClient != undefined && this.publicationDocument.mailsClient.length > 0)
          this.overridePublicationClientMail = true;

        if (this.cfeDocument.affaireAddress != null && this.cfeDocument.affaireAddress != undefined && this.cfeDocument.affaireAddress != "")
          this.overrideCfeAffaireAddress = true;
        if (this.cfeDocument.affaireRecipient != null && this.cfeDocument.affaireRecipient != undefined && this.cfeDocument.affaireRecipient != "")
          this.overrideCfeAffaireAddress = true;
        if (this.cfeDocument.clientAddress != null && this.cfeDocument.clientAddress != undefined && this.cfeDocument.clientAddress != "")
          this.overrideCfeClientAddress = true;
        if (this.cfeDocument.clientRecipient != null && this.cfeDocument.clientRecipient != undefined && this.cfeDocument.clientRecipient != "")
          this.overrideCfeClientAddress = true;

        if (this.cfeDocument.mailsAffaire != null && this.cfeDocument.mailsAffaire != undefined && this.cfeDocument.mailsAffaire.length > 0)
          this.overrideCfeAffaireMail = true;
        if (this.cfeDocument.mailsClient != null && this.cfeDocument.mailsClient != undefined && this.cfeDocument.mailsClient.length > 0)
          this.overrideCfeClientMail = true;

        if (this.kbisDocument.affaireAddress != null && this.kbisDocument.affaireAddress != undefined && this.kbisDocument.affaireAddress != "")
          this.overrideKbisAffaireAddress = true;
        if (this.kbisDocument.affaireRecipient != null && this.kbisDocument.affaireRecipient != undefined && this.kbisDocument.affaireRecipient != "")
          this.overrideKbisAffaireAddress = true;
        if (this.kbisDocument.clientAddress != null && this.kbisDocument.clientAddress != undefined && this.kbisDocument.clientAddress != "")
          this.overrideKbisClientAddress = true;
        if (this.kbisDocument.clientRecipient != null && this.kbisDocument.clientRecipient != undefined && this.kbisDocument.clientRecipient != "")
          this.overrideKbisClientAddress = true;

        if (this.kbisDocument.mailsAffaire != null && this.kbisDocument.mailsAffaire != undefined && this.kbisDocument.mailsAffaire.length > 0)
          this.overrideKbisAffaireMail = true;
        if (this.kbisDocument.mailsClient != null && this.kbisDocument.mailsClient != undefined && this.kbisDocument.mailsClient.length > 0)
          this.overrideKbisClientMail = true;
      })
      this.documentManagementForm.markAllAsTouched();
    }
  }

  ngOnInit() {
  }


  documentManagementForm = this.formBuilder.group({
    publicationRecipientTypeAffaire: [''],
    publicationRecipientTypeClient: [''],
    publicationMailingTypePaper: [''],
    publicationMailingTypePdf: [''],
    publicationNumberMailingAffaire: [''],
    publicationNumberMailingClient: [''],
    publicationAffaireRecipient: ['', Validators.maxLength(40)],
    publicationClientRecipient: ['', Validators.maxLength(40)],
    publicationClientMails: [''],
    publicationAffaireAddress: ['', Validators.maxLength(60)],
    publicationClientAddress: ['', Validators.maxLength(60)],
    publicationMailsClient: [''],
    publicationMailsAffaire: [''],
    cfeRecipientTypeAffaire: [''],
    cfeRecipientTypeClient: [''],
    cfeMailingTypePaper: [''],
    cfeMailingTypePdf: [''],
    cfeAffaireRecipient: ['', Validators.maxLength(40)],
    cfeClientRecipient: ['', Validators.maxLength(40)],
    cfeClientMails: [''],
    cfeAffaireAddress: ['', Validators.maxLength(60)],
    cfeClientAddress: ['', Validators.maxLength(60)],
    cfeMailsClient: [''],
    cfeMailsAffaire: [''],
    kbisRecipientTypeAffaire: [''],
    kbisRecipientTypeClient: [''],
    kbisMailingTypePaper: [''],
    kbisMailingTypePdf: [''],
    kbisAffaireRecipient: ['', Validators.maxLength(40)],
    kbisClientRecipient: ['', Validators.maxLength(40)],
    kbisClientMails: [''],
    kbisAffaireAddress: ['', Validators.maxLength(60)],
    kbisClientAddress: ['', Validators.maxLength(60)],
    kbisMailsClient: [''],
    kbisMailsAffaire: [''],
  });

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
    this.documentManagementForm.get(formFieldName)?.setValue(null);
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
    this.documentManagementForm.get(formFieldName)?.setValue(null);
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

  getFormStatus(): boolean {
    return this.documentManagementForm.valid;
  }

}
