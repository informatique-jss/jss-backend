import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { Document } from '../../model/Document';
import { Mail } from '../../model/Mail';

@Component({
  selector: 'addressing',
  templateUrl: './addressing.component.html',
  styleUrls: ['./addressing.component.css']
})
export class AddressingComponent implements OnInit {
  @Input() document: Document = {} as Document;
  @Output() documentChange: EventEmitter<Document> = new EventEmitter<Document>();
  @Input() editMode: boolean = false;
  @Input() displayPaperCopy: boolean = false;
  @Input() hideAdressing: boolean = false;
  @Input() hideConsignee: boolean = false;
  @Input() displayMailCC: boolean = false;
  @Input() displayNumberMailing: boolean = false;
  @Input() onlyMail: boolean = false;
  @Input() hideOverriding: boolean = false;

  overrideAffaireAddress: boolean = false;
  overrideAffaireMail: boolean = false;
  overrideAffaireMailCC: boolean = false;
  overrideClientAddress: boolean = false;
  overrideClientMail: boolean = false;

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;

  constructor(private formBuilder: UntypedFormBuilder, protected tiersService: TiersService) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    this.overrideAffaireAddress = false;
    this.overrideAffaireMail = false;
    this.overrideAffaireMailCC = false;
    this.overrideClientAddress = false;
    this.overrideClientMail = false;

    if (changes.document != undefined) {
      if (!this.document.isMailingPaper)
        this.document.isMailingPaper = false;
      if (!this.document.isMailingPdf)
        this.document.isMailingPdf = false;
      if (!this.document.isRecipientAffaire)
        this.document.isRecipientAffaire = false;
      if (!this.document.isRecipientClient)
        this.document.isRecipientClient = false;

      if (this.document.affaireAddress && this.document.affaireAddress.length > 0)
        this.overrideAffaireAddress = true;
      if (this.document.affaireRecipient && this.document.affaireRecipient.length > 0)
        this.overrideAffaireAddress = true;
      if (this.document.clientAddress && this.document.clientAddress.length > 0)
        this.overrideClientAddress = true;
      if (this.document.clientRecipient && this.document.clientRecipient.length > 0)
        this.overrideClientAddress = true;

      if (this.document.mailsAffaire && this.document.mailsAffaire.length > 0)
        this.overrideAffaireMail = true;
      if (this.document.mailsCCResponsableAffaire && this.document.mailsCCResponsableAffaire.length > 0)
        this.overrideAffaireMailCC = true;
      if (this.document.mailsCCResponsableClient && this.document.mailsCCResponsableClient.length > 0)
        this.overrideClientMail = true;
      if (this.document.mailsClient && this.document.mailsClient.length > 0)
        this.overrideClientMail = true;

      if (this.onlyMail)
        this.document.isMailingPdf = true;

    }
    this.adressingForm.markAllAsTouched();
  }

  adressingForm = this.formBuilder.group({
  });

  getResponsables(): Responsable[] {
    let tiers = this.tiersService.getCurrentViewedTiers();
    let responsableViewed = this.tiersService.getCurrentViewedResponsable();
    let responsables = [] as Array<Responsable>;
    if (tiers != null) {
      if (tiers.responsables != null && tiers.responsables.length > 0) {
        tiers.responsables.forEach(responsable => {
          if (responsableViewed == null || responsable.id != responsableViewed.id)
            responsables.push(responsable);
        })
      }
    }
    return responsables;
  }

  removeAllMailClient(document: Document): void {
    document.mailsClient = [] as Array<Mail>;
  }


  removeAllMailAffaire(document: Document): void {
    document.mailsAffaire = [] as Array<Mail>;
  }

}
