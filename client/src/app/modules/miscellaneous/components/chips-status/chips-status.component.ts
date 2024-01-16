import { Component, Input, OnInit } from '@angular/core';
import { CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT, QUOTATION_STATUS_REFUSED_BY_CUSTOMER, QUOTATION_STATUS_TO_VERIFY, VALIDATED_BY_CUSTOMER } from 'src/app/libs/Constants';
import { ConstantService } from '../../services/constant.service';

@Component({
  selector: 'chips-status',
  templateUrl: './chips-status.component.html',
  styleUrls: ['./chips-status.component.css']
})
export class ChipsStatusComponent implements OnInit {

  /**
   * Status code to parse
   */
  @Input() status: string = '';
  /**
   * Value to display
   */
  @Input() value: string = '';
  statusType = 4;

  okStatus: string[] = [VALIDATED_BY_CUSTOMER, CUSTOMER_ORDER_STATUS_BILLED, this.constantService.getInvoiceStatusPayed().code, this.constantService.getInvoiceStatusCreditNoteEmited().code];
  warnStatus: string[] = [QUOTATION_STATUS_TO_VERIFY, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT, this.constantService.getInvoiceStatusSend().code, this.constantService.getInvoiceStatusReceived().code, this.constantService.getInvoiceStatusCreditNoteReceived().code];
  koStatus: string[] = [QUOTATION_STATUS_REFUSED_BY_CUSTOMER, this.constantService.getInvoiceStatusCancelled().code];

  constructor(
    private constantService: ConstantService
  ) { }

  ngOnInit() {
    if (this.okStatus.indexOf(this.status) >= 0)
      this.statusType = 1;
    if (this.warnStatus.indexOf(this.status) >= 0)
      this.statusType = 2;
    if (this.koStatus.indexOf(this.status) >= 0)
      this.statusType = 3;
  }

}
