import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { Mail } from '../../../profile/model/Mail';
import { Responsable } from '../../../profile/model/Responsable';
import { ResponsableService } from '../../../profile/services/responsable.service';
import { Document } from '../../model/Document';
import { DocumentService } from '../../services/document.service';

@Component({
  selector: 'app-edit-address',
  templateUrl: './edit-address.component.html',
  styleUrls: ['./edit-address.component.css']
})
export class EditAddressComponent implements OnInit {

  idOrder: number | undefined;
  idQuotation: number | undefined;
  idResponsable: number | undefined;
  documents: Document[] | undefined;
  documentForm = this.formBuilder.group({});
  responsable: Responsable | undefined;

  documentTypeBilling = this.constantService.getDocumentTypeBilling();
  documentTypeDigital = this.constantService.getDocumentTypeDigital();
  documentTypePaper = this.constantService.getDocumentTypePaper();

  billingLabelTypeAffaire = this.constantService.getBillingLabelTypeCodeAffaire();
  billingLabelTypeCustomer = this.constantService.getBillingLabelTypeCustomer();
  billingLabelTypeOther = this.constantService.getBillingLabelTypeOther();

  lockBillingLabel: boolean = false;

  newMailBillingAffaire: string = "";
  newMailBillingClient: string = "";

  newMailDigitalAffaire: string = "";
  newMailDigitalClient: string = "";

  constructor(private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private documentService: DocumentService,
    private constantService: ConstantService,
    private responsableService: ResponsableService,
    private appService: AppService,) { }

  capitalizeName = capitalizeName;

  ngOnInit() {
    this.idOrder = this.activatedRoute.snapshot.params['idOrder'];
    this.idQuotation = this.activatedRoute.snapshot.params['idQuotation'];
    this.idResponsable = this.activatedRoute.snapshot.params['idResponsable'];
    if (this.idOrder)
      this.documentService.getDocumentForCustomerOrder(this.idOrder).subscribe(response => {
        this.documents = response.sort((a: Document, b: Document) => b.documentType.code.localeCompare(a.documentType.code));

        if (this.documents)
          for (let document of this.documents)
            if (document.documentType.id == this.documentTypeBilling.id && document.billingLabelType) {
              if (document.billingLabelType.id == this.billingLabelTypeAffaire.id)
                document.billingLabelType = this.billingLabelTypeAffaire;
              if (document.billingLabelType.id == this.billingLabelTypeCustomer.id)
                document.billingLabelType = this.billingLabelTypeCustomer;
              if (document.billingLabelType.id == this.billingLabelTypeOther.id)
                document.billingLabelType = this.billingLabelTypeOther;

              this.lockBillingLabel = document.billingLabelType.id == this.billingLabelTypeOther.id;
            }
      })
    if (this.idQuotation)
      this.documentService.getDocumentForQuotation(this.idQuotation).subscribe(response => {
        this.documents = response.sort((a: Document, b: Document) => b.documentType.code.localeCompare(a.documentType.code));

        if (this.documents)
          for (let document of this.documents)
            if (document.documentType.id == this.documentTypeBilling.id && document.billingLabelType) {
              if (document.billingLabelType.id == this.billingLabelTypeAffaire.id)
                document.billingLabelType = this.billingLabelTypeAffaire;
              if (document.billingLabelType.id == this.billingLabelTypeCustomer.id)
                document.billingLabelType = this.billingLabelTypeCustomer;
              if (document.billingLabelType.id == this.billingLabelTypeOther.id)
                document.billingLabelType = this.billingLabelTypeOther;

              this.lockBillingLabel = document.billingLabelType.id == this.billingLabelTypeOther.id;
            }
      })
    if (this.idResponsable) {
      this.responsableService.getResponsable(this.idResponsable).subscribe(response => {
        this.responsable = response;
      })
      this.documentService.getDocumentForResponsable(this.idResponsable).subscribe(response => {
        this.documents = response.sort((a: Document, b: Document) => b.documentType.code.localeCompare(a.documentType.code));

        if (this.documents)
          for (let document of this.documents)
            if (document.documentType.id == this.documentTypeBilling.id && document.billingLabelType) {
              if (document.billingLabelType.id == this.billingLabelTypeAffaire.id)
                document.billingLabelType = this.billingLabelTypeAffaire;
              if (document.billingLabelType.id == this.billingLabelTypeCustomer.id)
                document.billingLabelType = this.billingLabelTypeCustomer;
              if (document.billingLabelType.id == this.billingLabelTypeOther.id)
                document.billingLabelType = this.billingLabelTypeOther;

              this.lockBillingLabel = document.billingLabelType.id == this.billingLabelTypeOther.id;
            }
      })
    }
  }

  saveDocuments() {
    if (this.documents) {
      if (this.idOrder)
        this.documentService.addOrUpdateDocumentsForCustomerOrder(this.documents).subscribe(response => {
          this.appService.openRoute(null, "account/orders/details/" + this.idOrder, undefined);
        })

      if (this.idQuotation)
        this.documentService.addOrUpdateDocumentsForQuotation(this.documents).subscribe(response => {
          this.appService.openRoute(null, "account/quotations/details/" + this.idQuotation, undefined);
        })

      if (this.idResponsable)
        this.documentService.addOrUpdateDocumentsForResponsable(this.documents).subscribe(response => {
          this.appService.openRoute(null, "account/settings/" + this.idResponsable, undefined);
        })
    }
  }

  cancelDocuments() {
    if (this.idOrder)
      this.appService.openRoute(null, "account/orders/details/" + this.idOrder, undefined);
    if (this.idQuotation)
      this.appService.openRoute(null, "account/quotations/details/" + this.idQuotation, undefined);
    if (this.idResponsable)
      this.appService.openRoute(null, "account/settings/" + this.idResponsable, undefined);
  }

  deleteMail(mail: Mail, document: Document, isAffaire: boolean) {
    if (document)
      if (isAffaire)
        document.mailsAffaire.splice(document.mailsAffaire.indexOf(mail), 1);
      else
        document.mailsClient.splice(document.mailsClient.indexOf(mail), 1);
  }

  addMail(document: Document, isAffaire: boolean) {
    if (document)
      if (isAffaire && this.newMailBillingAffaire && validateEmail(this.newMailBillingAffaire)) {
        let mail = {} as Mail;
        mail.mail = this.newMailBillingAffaire;
        if (!document.mailsAffaire)
          document.mailsAffaire = [];
        document.mailsAffaire.push(mail);
        this.newMailBillingAffaire = "";
      } else if (this.newMailBillingClient && validateEmail(this.newMailBillingClient)) {
        let mail = {} as Mail;
        mail.mail = this.newMailBillingClient;
        if (!document.mailsClient)
          document.mailsClient = [];
        document.mailsClient.push(mail);
        this.newMailBillingClient = "";
      }
  }

  addMailDigital(document: Document, isAffaire: boolean) {
    if (document)
      if (isAffaire && this.newMailDigitalAffaire && validateEmail(this.newMailDigitalAffaire)) {
        let mail = {} as Mail;
        mail.mail = this.newMailDigitalAffaire;
        if (!document.mailsAffaire)
          document.mailsAffaire = [];
        document.mailsAffaire.push(mail);
        this.newMailDigitalAffaire = "";
      } else if (this.newMailDigitalClient && validateEmail(this.newMailDigitalClient)) {
        let mail = {} as Mail;
        mail.mail = this.newMailDigitalClient;
        if (!document.mailsClient)
          document.mailsClient = [];
        document.mailsClient.push(mail);
        this.newMailDigitalClient = "";
      }
  }

}
