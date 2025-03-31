import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { AppService } from '../../../../libs/app.service';
import { JSS_BIC, JSS_IBAN } from '../../../../libs/Constants';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { InvoicingSummary } from '../../model/InvoicingSummary';
import { MailComputeResult } from '../../model/MailComputeResult';
import { MyJssImage } from '../../model/MyJssImage';
import { InvoicingSummaryService } from '../../services/invoicing.summary.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';
import { MyJssImageService } from '../../services/my.jss.image.service';
import { initTooltips } from '../orders/orders.component';

@Component({
    selector: 'app-pay-quotation',
    templateUrl: './pay-quotation.component.html',
    styleUrls: ['./pay-quotation.component.css'],
    standalone: false
})
export class PayQuotationComponent implements OnInit {
  idQuotation: number | undefined;
  invoiceSummary: InvoicingSummary | undefined;
  qrCodeRecourse: SafeResourceUrl | undefined;
  qrCodeImage: MyJssImage | undefined;
  quotationMailComputeResult: MailComputeResult | undefined;
  defaultMail: string = "";
  JSS_IBAN = JSS_IBAN;
  JSS_BIC = JSS_BIC;

  constructor(private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private invoicingSummaryService: InvoicingSummaryService,
    private mailComputeResultService: MailComputeResultService,
    private myJssImageService: MyJssImageService,
    private sanitizer: DomSanitizer,
    private appService: AppService,) { }

  payOrderForm = this.formBuilder.group({});

  ngOnInit() {
    this.idQuotation = this.activatedRoute.snapshot.params['idQuotation'];
    if (this.idQuotation) {
      this.mailComputeResultService.getMailComputeResultForBillingForQuotation(this.idQuotation).subscribe(response => {
        this.quotationMailComputeResult = response;
        if (this.quotationMailComputeResult && this.quotationMailComputeResult.recipientsMailTo && this.quotationMailComputeResult.recipientsMailTo.length > 0)
          this.defaultMail = this.quotationMailComputeResult.recipientsMailTo[0].mail;
        this.refreshQrCode();
      })
      this.invoicingSummaryService.getInvoicingSummaryForQuotation(this.idQuotation).subscribe(response => {
        this.invoiceSummary = response;
        initTooltips();
      })
    }
  }

  cancelPay() {
    this.appService.openRoute(null, "account/quotations/details/" + this.idQuotation, undefined);
  }

  refreshQrCode() {
    if (this.defaultMail && this.defaultMail.length > 0 && validateEmail(this.defaultMail) && this.idQuotation)
      this.myJssImageService.downloadQrCode(this.idQuotation, this.defaultMail).subscribe(response => {
        this.qrCodeImage = response;
        this.qrCodeRecourse = this.sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,' + this.qrCodeImage.data);
      })
    else
      this.qrCodeRecourse = undefined;
  }

  payedCb() {
    if (this.qrCodeImage)
      window.open(this.qrCodeImage.address, "_blank");
  }

  notifyCopied() {
    this.appService.displayToast("Copié dans le presse papier !", false, "Copié !", 5000);
  }

}
