import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { Document } from '../../../my-account/model/Document';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { Civility } from '../../../profile/model/Civility';
import { Country } from '../../../profile/model/Country';
import { Mail } from '../../../profile/model/Mail';
import { Responsable } from '../../../profile/model/Responsable';
import { Tiers } from '../../../profile/model/Tiers';
import { LoginService } from '../../../profile/services/login.service';
import { IQuotation } from '../../model/IQuotation';

@Component({
  selector: 'checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css'],
  standalone: false
})
export class CheckoutComponent implements OnInit {

  quotation: IQuotation | undefined;
  currentUser: Responsable | undefined;

  inputMail: string = "";
  isLinkSent: boolean = false;
  isSendingLink: boolean = false;

  notSignedResponsable: Responsable | undefined;
  checkedOnce: boolean = false;
  countryFrance: Country = this.constantService.getCountryFrance();

  validateEmail = validateEmail;
  validateFrenchPhone = validateFrenchPhone;
  validateInternationalPhone = validateInternationalPhone;

  documentTypeBilling = this.constantService.getDocumentTypeBilling();
  documentTypeDigital = this.constantService.getDocumentTypeDigital();
  documentTypePaper = this.constantService.getDocumentTypePaper();
  documents: Document[] = [];
  isExtRefMandatory: boolean = false;

  documentForm = this.formBuilder.group({});

  constructor(
    private loginService: LoginService,
    private quotationService: QuotationService,
    private orderService: CustomerOrderService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private constantService: ConstantService,
  ) { }

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
      if (this.currentUser) {
        this.documents = [];
        this.documents.push(...this.currentUser.documents);
      }
      this.initIQuotation();
    })

    if (!this.currentUser) {
      this.notSignedResponsable = { civility: {} as Civility, mail: {} as Mail, tiers: {} as Tiers } as Responsable;
      this.documents = this.initEmptyDocuments();
    }
    this.initIQuotation();
  }

  initEmptyDocuments(): Document[] {
    let document1: Document = { documentType: this.documentTypeBilling } as Document;
    let document2: Document = { documentType: this.documentTypeDigital } as Document;
    let document3: Document = { documentType: this.documentTypePaper } as Document;

    return [document1, document2, document3];
  }

  initIQuotation() {
    if (this.currentUser) {
      if (this.quotationService.getCurrentDraftQuotationId()) {
        this.quotationService.getQuotation(parseInt(this.quotationService.getCurrentDraftQuotationId()!)).subscribe(response => {
          this.quotation = response;
        });
      } else if (this.orderService.getCurrentDraftOrderId()) {
        this.orderService.getCustomerOrder(parseInt(this.orderService.getCurrentDraftOrderId()!)).subscribe(response => {
          this.quotation = response;
        });
      }
    } else {
      if (this.quotationService.getCurrentDraftQuotation()) {
        this.quotation = this.quotationService.getCurrentDraftQuotation()!;
      } else if (this.orderService.getCurrentDraftOrder()) {
        this.quotation = this.orderService.getCurrentDraftOrder()!;
      }
    }
  }

  sendConnectionLink() {
    if (validateEmail(this.inputMail)) {
      this.isSendingLink = true;
      this.loginService.sendConnectionLink(this.inputMail).subscribe(response => {
        this.isLinkSent = true;
        this.isSendingLink = false;
      })
    } else {
      this.appService.displayToast("L'adresse saisie n'est pas une adresse mail valide", true, "Erreur", 5000);
    }
  }

  computePrices() {
    //TODO
  }



  ngOnDestroy() {
  }

}
