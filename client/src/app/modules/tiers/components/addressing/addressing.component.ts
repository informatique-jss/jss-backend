import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { validateEmail } from 'src/app/libs/CustomFormsValidatorsHelper';
import { prepareMail } from 'src/app/libs/MailHelper';
import { ITiers } from '../../model/ITiers';
import { Mail } from '../../model/Mail';
import { Responsable } from '../../model/Responsable';
import { Tiers } from '../../model/Tiers';
import { TiersDocument } from '../../model/TiersDocument';
import { TiersService } from '../../services/tiers.service';
import { TiersComponent } from '../tiers/tiers.component';

@Component({
  selector: 'addressing',
  templateUrl: './addressing.component.html',
  styleUrls: ['./addressing.component.css']
})
export class AddressingComponent implements OnInit {
  @Input() document: TiersDocument = {} as TiersDocument;
  @Input() editMode: boolean = false;
  @Input() displayPaperCopy: boolean = false;
  @Input() hideAdressing: boolean = false;
  @Input() hideConsignee: boolean = false;
  @Input() displayMailCC: boolean = false;

  overrideAffaireAddress: boolean = false;
  overrideAffaireMail: boolean = false;
  overrideAffaireMailCC: boolean = false;
  overrideClientAddress: boolean = false;
  overrideClientMail: boolean = false;

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;

  constructor(private formBuilder: FormBuilder, protected tiersService: TiersService) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.document != undefined) {
      if (this.document.isMailingPaper == undefined || this.document.isMailingPaper == null)
        this.document.isMailingPaper = false;
      if (this.document.isMailingPdf == undefined || this.document.isMailingPdf == null)
        this.document.isMailingPdf = false;
      if (this.document.isRecipientAffaire == undefined || this.document.isRecipientAffaire == null)
        this.document.isRecipientAffaire = false;
      if (this.document.isRecipientClient == undefined || this.document.isRecipientClient == null)
        this.document.isRecipientClient = false;

      if (this.document.affaireAddress != null && this.document.affaireAddress != undefined && this.document.affaireAddress != "")
        this.overrideAffaireAddress = true;
      if (this.document.affaireRecipient != null && this.document.affaireRecipient != undefined && this.document.affaireRecipient != "")
        this.overrideAffaireAddress = true;
      if (this.document.clientAddress != null && this.document.clientAddress != undefined && this.document.clientAddress != "")
        this.overrideClientAddress = true;
      if (this.document.clientRecipient != null && this.document.clientRecipient != undefined && this.document.clientRecipient != "")
        this.overrideClientAddress = true;

      if (this.document.mailsAffaire != null && this.document.mailsAffaire != undefined && this.document.mailsAffaire.length > 0)
        this.overrideAffaireMail = true;
      if (this.document.mailsCCResponsableAffaire != null && this.document.mailsCCResponsableAffaire != undefined && this.document.mailsCCResponsableAffaire.length > 0)
        this.overrideAffaireMailCC = true;
      if (this.document.mailsClient != null && this.document.mailsClient != undefined && this.document.mailsClient.length > 0)
        this.overrideClientMail = true;

    }
    this.documentManagementForm.markAllAsTouched();
  }

  documentManagementForm = this.formBuilder.group({
    RecipientTypeAffaire: [''],
    RecipientTypeClient: [''],
    MailingTypePaper: [''],
    MailingTypePdf: [''],
    NumberMailingAffaire: [''],
    NumberMailingClient: [''],
    AffaireRecipient: ['', Validators.maxLength(40)],
    ClientRecipient: ['', Validators.maxLength(40)],
    ClientMails: [''],
    AffaireAddress: ['', Validators.maxLength(60)],
    ClientAddress: ['', Validators.maxLength(60)],
    MailsClient: [''],
    MailsClientCC: [''],
    MailsAffaire: [''],
    MailsAffaireCC: [''],
  });

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

}
