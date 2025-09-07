import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { NgbNavModule, NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { JSS_BIC, JSS_IBAN } from '../../../../libs/Constants';
import { CopyClipboardDirective } from '../../../../libs/CopyClipboard.directive';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { InvoicingSummary } from '../../model/InvoicingSummary';
import { MailComputeResult } from '../../model/MailComputeResult';
import { MyJssImage } from '../../model/MyJssImage';
import { InvoicingSummaryService } from '../../services/invoicing.summary.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';
import { MyJssImageService } from '../../services/my.jss.image.service';

@Component({
  selector: 'app-pay-quotation',
  templateUrl: './pay-quotation.component.html',
  styleUrls: ['./pay-quotation.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent, CopyClipboardDirective, NgbNavModule, NgbTooltip]
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

  payOrderForm!: FormGroup;

  ngOnInit() {
    this.payOrderForm = this.formBuilder.group({});
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
      })
    }
  }

  refreshQrCode() {
    if (this.defaultMail && this.defaultMail.length > 0 && validateEmail(this.defaultMail) && this.idQuotation)
      this.myJssImageService.downloadQrCodeForQuotation(this.idQuotation, this.defaultMail).subscribe(response => {
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
