import { Component, Input, OnInit } from '@angular/core';
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

  okStatus: string[] = ["VALIDATED_BY_JSS", "VALIDATED_BY_CUSTOMER", "BILLED", "PAYED", "SENT", this.constantService.getInvoiceStatusPayed().code, this.constantService.getInvoiceStatusCreditNoteEmited().code];
  warnStatus: string[] = ["TO_VERIFY", "WAITING_DEPOSIT", "WAITING", this.constantService.getInvoiceStatusSend().code, this.constantService.getInvoiceStatusReceived().code, this.constantService.getInvoiceStatusCreditNoteReceived().code];
  koStatus: string[] = ["REFUSED_BY_CUSTOMER", this.constantService.getInvoiceStatusCancelled().code];

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
