import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { Observable, Subscription } from 'rxjs';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { CFE_TIERS_DOCUMENT_TYPE_CODE, KBIS_TIERS_DOCUMENT_TYPE_CODE, PUBLICATION_TIERS_DOCUMENT_TYPE_CODE, SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { validateEmail } from 'src/app/libs/CustomFormsValidatorsHelper';
import { prepareMail } from 'src/app/libs/MailHelper';
import { Mail } from '../../model/Mail';
import { Tiers } from '../../model/Tiers';
import { TiersDocument } from '../../model/TiersDocument';
import { TiersDocumentType } from '../../model/TiersDocumentType';
import { TiersDocumentTypeService } from '../../services/tiers.document.type.service';

@Component({
  selector: 'document-management',
  templateUrl: './document-management.component.html',
  styleUrls: ['./document-management.component.css']
})
export class DocumentManagementComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();

  @Input() tiers: Tiers = {} as Tiers;
  @Input() editMode: boolean = false;

  @Input() eventsTiersLoaded: Observable<void> | undefined;
  private eventsTiersLoadedSubscription: Subscription | undefined;

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

  ngOnInit() {
    this.eventsTiersLoadedSubscription = this.eventsTiersLoaded?.subscribe(response => {
      if (this.tiers.documents != null && this.tiers.documents != undefined && this.tiers.documents.length > 0)
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
    })

    this.tiersDocumentTypeService.getDocumentTypes().subscribe(response => {
      this.tiersDocumentTypes = response;
      this.setPublicationDocument();
      this.setCfeDocument();
      this.setKbisDocument();

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
  }

  ngOnDestroy() {
    this.eventsTiersLoadedSubscription?.unsubscribe();
  }


  documentManagementForm = this.formBuilder.group({
    publicationRecipientTypeAffaire: [''],
    publicationRecipientTypeClient: [''],
    publicationMailingTypePaper: [''],
    publicationMailingTypePdf: [''],
    publicationNumberMailingAffaire: [''],
    publicationNumberMailingClient: [''],
    publicationAffaireRecipient: [''],
    publicationClientRecipient: [''],
    publicationClientMails: [''],
    publicationAffaireAddress: [''],
    publicationClientAddress: [''],
    publicationMailsClient: [''],
    publicationMailsAffaire: [''],
    cfeRecipientTypeAffaire: [''],
    cfeRecipientTypeClient: [''],
    cfeMailingTypePaper: [''],
    cfeMailingTypePdf: [''],
    cfeNumberMailingAffaire: [''],
    cfeNumberMailingClient: [''],
    cfeAffaireRecipient: [''],
    cfeClientRecipient: [''],
    cfeClientMails: [''],
    cfeAffaireAddress: [''],
    cfeClientAddress: [''],
    cfeMailsClient: [''],
    cfeMailsAffaire: [''],
    kbisRecipientTypeAffaire: [''],
    kbisRecipientTypeClient: [''],
    kbisMailingTypePaper: [''],
    kbisMailingTypePdf: [''],
    kbisNumberMailingAffaire: [''],
    kbisNumberMailingClient: [''],
    kbisAffaireRecipient: [''],
    kbisClientRecipient: [''],
    kbisClientMails: [''],
    kbisAffaireAddress: [''],
    kbisClientAddress: [''],
    kbisMailsClient: [''],
    kbisMailsAffaire: [''],
  });

  setPublicationDocument(): void {
    if (this.tiers == null || this.getDocumentType(PUBLICATION_TIERS_DOCUMENT_TYPE_CODE).id == undefined)
      this.publicationDocument = {} as TiersDocument;
    if (this.tiers.documents == null || this.tiers.documents == undefined) {
      this.tiers.documents = [] as Array<TiersDocument>;
      let doc = {} as TiersDocument;
      doc.tiersDocumentType = this.getDocumentType(PUBLICATION_TIERS_DOCUMENT_TYPE_CODE);
      this.tiers.documents.push(doc);
      this.publicationDocument = this.tiers.documents[0];
      return;
    }

    if (this.tiers.documents.length > 0) {
      for (let i = 0; i < this.tiers.documents.length; i++) {
        const document = this.tiers.documents[i];
        if (document.tiersDocumentType.code == PUBLICATION_TIERS_DOCUMENT_TYPE_CODE) {
          this.publicationDocument = document;
          return;
        }
      }
    }

    let doc = {} as TiersDocument;
    doc.tiersDocumentType = this.getDocumentType(PUBLICATION_TIERS_DOCUMENT_TYPE_CODE);
    this.tiers.documents.push(doc);
    this.publicationDocument = this.tiers.documents[this.tiers.documents.length - 1];
  }

  setCfeDocument(): void {
    if (this.tiers == null || this.getDocumentType(CFE_TIERS_DOCUMENT_TYPE_CODE).id == undefined)
      this.cfeDocument = {} as TiersDocument;
    if (this.tiers.documents == null || this.tiers.documents == undefined) {
      this.tiers.documents = [] as Array<TiersDocument>;
      let doc = {} as TiersDocument;
      doc.tiersDocumentType = this.getDocumentType(CFE_TIERS_DOCUMENT_TYPE_CODE);
      this.tiers.documents.push(doc);
      this.cfeDocument = this.tiers.documents[0];
      return;
    }

    if (this.tiers.documents.length > 0) {
      for (let i = 0; i < this.tiers.documents.length; i++) {
        const document = this.tiers.documents[i];
        if (document.tiersDocumentType.code == CFE_TIERS_DOCUMENT_TYPE_CODE) {
          this.cfeDocument = document;
          return;
        }
      }
    }

    let doc = {} as TiersDocument;
    doc.tiersDocumentType = this.getDocumentType(CFE_TIERS_DOCUMENT_TYPE_CODE);
    this.tiers.documents.push(doc);
    this.cfeDocument = this.tiers.documents[this.tiers.documents.length - 1];
  }

  setKbisDocument(): void {
    if (this.tiers == null || this.getDocumentType(KBIS_TIERS_DOCUMENT_TYPE_CODE).id == undefined)
      this.kbisDocument = {} as TiersDocument;
    if (this.tiers.documents == null || this.tiers.documents == undefined) {
      this.tiers.documents = [] as Array<TiersDocument>;
      let doc = {} as TiersDocument;
      doc.tiersDocumentType = this.getDocumentType(KBIS_TIERS_DOCUMENT_TYPE_CODE);
      this.tiers.documents.push(doc);
      this.kbisDocument = this.tiers.documents[0];
      return;
    }

    if (this.tiers.documents.length > 0) {
      for (let i = 0; i < this.tiers.documents.length; i++) {
        const document = this.tiers.documents[i];
        if (document.tiersDocumentType.code == KBIS_TIERS_DOCUMENT_TYPE_CODE) {
          this.kbisDocument = document;
          return;
        }
      }
    }

    let doc = {} as TiersDocument;
    doc.tiersDocumentType = this.getDocumentType(KBIS_TIERS_DOCUMENT_TYPE_CODE);
    this.tiers.documents.push(doc);
    this.kbisDocument = this.tiers.documents[this.tiers.documents.length - 1];
  }

  getDocumentType(codeTypeDocument: string): TiersDocumentType {
    if (this.tiersDocumentTypes.length > 0) {
      for (let i = 0; i < this.tiersDocumentTypes.length; i++) {
        const tiersDocumentType = this.tiersDocumentTypes[i];
        if (tiersDocumentType.code == codeTypeDocument)
          return tiersDocumentType;
      }
    }
    return {} as TiersDocumentType;
  }

  addMailPublicationClient(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    let mail: Mail = {} as Mail;
    if (value && validateEmail(value)) {
      mail.mail = value;
      if (this.publicationDocument.mailsClient == undefined || this.publicationDocument.mailsClient == null)
        this.publicationDocument.mailsClient = [] as Mail[];
      this.publicationDocument.mailsClient.push(mail);
    }
    event.chipInput!.clear();
    this.documentManagementForm.get("publicationClientMails")?.setValue(null);
  }


  addMailCfeClient(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    let mail: Mail = {} as Mail;
    if (value && validateEmail(value)) {
      mail.mail = value;
      if (this.cfeDocument.mailsClient == undefined || this.cfeDocument.mailsClient == null)
        this.cfeDocument.mailsClient = [] as Mail[];
      this.cfeDocument.mailsClient.push(mail);
    }
    event.chipInput!.clear();
    this.documentManagementForm.get("cfeClientMails")?.setValue(null);
  }

  addMailKbisClient(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    let mail: Mail = {} as Mail;
    if (value && validateEmail(value)) {
      mail.mail = value;
      if (this.kbisDocument.mailsClient == undefined || this.kbisDocument.mailsClient == null)
        this.kbisDocument.mailsClient = [] as Mail[];
      this.kbisDocument.mailsClient.push(mail);
    }
    event.chipInput!.clear();
    this.documentManagementForm.get("kbisClientMails")?.setValue(null);
  }


  removeMailPublicationClient(inputMail: Mail): void {
    if (this.publicationDocument.mailsClient != undefined && this.publicationDocument.mailsClient != null && this.editMode)
      for (let i = 0; i < this.publicationDocument.mailsClient.length; i++) {
        const mail = this.publicationDocument.mailsClient[i];
        if (mail.mail == inputMail.mail) {
          this.publicationDocument.mailsClient.splice(i, 1);
          return;
        }
      }
  }

  removeMailCfeClient(inputMail: Mail): void {
    if (this.cfeDocument.mailsClient != undefined && this.cfeDocument.mailsClient != null && this.editMode)
      for (let i = 0; i < this.cfeDocument.mailsClient.length; i++) {
        const mail = this.cfeDocument.mailsClient[i];
        if (mail.mail == inputMail.mail) {
          this.cfeDocument.mailsClient.splice(i, 1);
          return;
        }
      }
  }

  removeMailKbisClient(inputMail: Mail): void {
    if (this.kbisDocument.mailsClient != undefined && this.kbisDocument.mailsClient != null && this.editMode)
      for (let i = 0; i < this.kbisDocument.mailsClient.length; i++) {
        const mail = this.kbisDocument.mailsClient[i];
        if (mail.mail == inputMail.mail) {
          this.kbisDocument.mailsClient.splice(i, 1);
          return;
        }
      }
  }

  removeAllMailPublicationClient(): void {
    this.publicationDocument.mailsClient = [] as Array<Mail>;
  }

  removeAllMailCfeClient(): void {
    this.cfeDocument.mailsClient = [] as Array<Mail>;
  }

  removeAllMailKbisClient(): void {
    this.kbisDocument.mailsClient = [] as Array<Mail>;
  }

  addMailPublicationAffaire(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    let mail: Mail = {} as Mail;
    if (value && validateEmail(value)) {
      mail.mail = value;
      if (this.publicationDocument.mailsAffaire == undefined || this.publicationDocument.mailsAffaire == null)
        this.publicationDocument.mailsAffaire = [] as Mail[];
      this.publicationDocument.mailsAffaire.push(mail);
    }
    event.chipInput!.clear();
    this.documentManagementForm.get("publicationClientMails")?.setValue(null);
  }

  addMailCfeAffaire(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    let mail: Mail = {} as Mail;
    if (value && validateEmail(value)) {
      mail.mail = value;
      if (this.cfeDocument.mailsAffaire == undefined || this.cfeDocument.mailsAffaire == null)
        this.cfeDocument.mailsAffaire = [] as Mail[];
      this.cfeDocument.mailsAffaire.push(mail);
    }
    event.chipInput!.clear();
    this.documentManagementForm.get("cfeClientMails")?.setValue(null);
  }

  addMailKbisAffaire(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    let mail: Mail = {} as Mail;
    if (value && validateEmail(value)) {
      mail.mail = value;
      if (this.kbisDocument.mailsAffaire == undefined || this.kbisDocument.mailsAffaire == null)
        this.kbisDocument.mailsAffaire = [] as Mail[];
      this.kbisDocument.mailsAffaire.push(mail);
    }
    event.chipInput!.clear();
    this.documentManagementForm.get("kbisClientMails")?.setValue(null);
  }

  removeMailPublicationAffaire(inputMail: Mail): void {
    if (this.publicationDocument.mailsAffaire != undefined && this.publicationDocument.mailsAffaire != null && this.editMode)
      for (let i = 0; i < this.publicationDocument.mailsAffaire.length; i++) {
        const mail = this.publicationDocument.mailsAffaire[i];
        if (mail.mail == inputMail.mail) {
          this.publicationDocument.mailsAffaire.splice(i, 1);
          return;
        }
      }
  }

  removeMailCfeAffaire(inputMail: Mail): void {
    if (this.cfeDocument.mailsAffaire != undefined && this.cfeDocument.mailsAffaire != null && this.editMode)
      for (let i = 0; i < this.cfeDocument.mailsAffaire.length; i++) {
        const mail = this.cfeDocument.mailsAffaire[i];
        if (mail.mail == inputMail.mail) {
          this.cfeDocument.mailsAffaire.splice(i, 1);
          return;
        }
      }
  }

  removeMailKbisAffaire(inputMail: Mail): void {
    if (this.kbisDocument.mailsAffaire != undefined && this.kbisDocument.mailsAffaire != null && this.editMode)
      for (let i = 0; i < this.kbisDocument.mailsAffaire.length; i++) {
        const mail = this.kbisDocument.mailsAffaire[i];
        if (mail.mail == inputMail.mail) {
          this.kbisDocument.mailsAffaire.splice(i, 1);
          return;
        }
      }
  }

  removeAllMailPublicationAffaire(): void {
    this.publicationDocument.mailsAffaire = [] as Array<Mail>;
  }

  removeAllMailCfeAffaire(): void {
    this.cfeDocument.mailsAffaire = [] as Array<Mail>;
  }

  removeAllMailKbisAffaire(): void {
    this.kbisDocument.mailsAffaire = [] as Array<Mail>;
  }

  prepareMail = function (mail: Mail) {
    prepareMail(mail.mail, null, null);
  }

  getFormStatus(): boolean {
    return this.documentManagementForm.valid;
  }

}
