import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Mail } from '../../../general/model/Mail';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { Responsable } from '../../../profile/model/Responsable';
import { ResponsableService } from '../../../profile/services/responsable.service';
import { BillingLabelType } from '../../model/BillingLabelType';
import { Document } from '../../model/Document';
import { DocumentType } from '../../model/DocumentType';
import { DocumentService } from '../../services/document.service';

@Component({
  selector: 'app-edit-address',
  templateUrl: './edit-address.component.html',
  styleUrls: ['./edit-address.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent, NgbNavModule]
})
export class EditAddressComponent implements OnInit {

  idOrder: number | undefined;
  idQuotation: number | undefined;
  idResponsable: number | undefined;
  documents: Document[] | undefined;

  responsable: Responsable | undefined;

  documentForm!: FormGroup;

  lockBillingLabel: boolean = false;

  newMailBillingAffaire: string = "";
  newMailBillingClient: string = "";

  newMailDigitalAffaire: string = "";
  newMailDigitalClient: string = "";

  documentTypeBilling!: DocumentType;
  documentTypeDigital!: DocumentType;
  documentTypePaper!: DocumentType;
  billingLabelTypeAffaire!: BillingLabelType;
  billingLabelTypeCustomer!: BillingLabelType;
  billingLabelTypeOther!: BillingLabelType;

  constructor(private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private documentService: DocumentService,
    private constantService: ConstantService,
    private responsableService: ResponsableService,
    private appService: AppService,) { }

  capitalizeName = capitalizeName;

  ngOnInit() {
    this.documentForm = this.formBuilder.group({});

    this.documentTypeBilling = this.constantService.getDocumentTypeBilling();
    this.documentTypeDigital = this.constantService.getDocumentTypeDigital();
    this.documentTypePaper = this.constantService.getDocumentTypePaper();

    this.billingLabelTypeAffaire = this.constantService.getBillingLabelTypeCodeAffaire();
    this.billingLabelTypeCustomer = this.constantService.getBillingLabelTypeCustomer();
    this.billingLabelTypeOther = this.constantService.getBillingLabelTypeOther();

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
