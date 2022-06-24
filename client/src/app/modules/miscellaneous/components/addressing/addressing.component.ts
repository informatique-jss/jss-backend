import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
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

      if (this.onlyMail)
        this.document.isMailingPdf = true;

    }
    this.adressingForm.markAllAsTouched();
  }

  adressingForm = this.formBuilder.group({
    mailsCCResponsableAffaire: ['', []],
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
