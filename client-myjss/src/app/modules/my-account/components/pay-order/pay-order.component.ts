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
  selector: 'app-pay-order',
  templateUrl: './pay-order.component.html',
  styleUrls: ['./pay-order.component.css']
})
export class PayOrderComponent implements OnInit {
  idOrder: number | undefined;
  invoiceSummary: InvoicingSummary | undefined;
  qrCodeRecourse: SafeResourceUrl | undefined;
  qrCodeImage: MyJssImage | undefined;
  orderMailComputeResult: MailComputeResult | undefined;
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
    this.idOrder = this.activatedRoute.snapshot.params['idOrder'];
    if (this.idOrder) {
      this.mailComputeResultService.getMailComputeResultForBillingForCustomerOrder(this.idOrder).subscribe(response => {
        this.orderMailComputeResult = response;
        if (this.orderMailComputeResult && this.orderMailComputeResult.recipientsMailTo && this.orderMailComputeResult.recipientsMailTo.length > 0)
          this.defaultMail = this.orderMailComputeResult.recipientsMailTo[0].mail;
        this.refreshQrCode();
      })
      this.invoicingSummaryService.getInvoicingSummaryForCustomerOrder(this.idOrder).subscribe(response => {
        this.invoiceSummary = response;
        initTooltips();
      })
    }
  }

  cancelPay() {
    this.appService.openRoute(null, "account/orders/details/" + this.idOrder, undefined);
  }

  refreshQrCode() {
    if (this.defaultMail && this.defaultMail.length > 0 && validateEmail(this.defaultMail) && this.idOrder)
      this.myJssImageService.downloadQrCode(this.idOrder, this.defaultMail).subscribe(response => {
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
