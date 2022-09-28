import { Component, Input, OnInit } from '@angular/core';

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

  okStatus: string[] = ["VALIDATED_BY_JSS", "VALIDATED_BY_CUSTOMER", "BILLED", "PAYED"];
  warnStatus: string[] = ["TO_VERIFY", "WAITING_DEPOSIT"];
  koStatus: string[] = ["REFUSED_BY_CUSTOMER"];

  constructor() { }

  ngOnInit() {
    if (this.okStatus.indexOf(this.status) >= 0)
      this.statusType = 1;
    if (this.warnStatus.indexOf(this.status) >= 0)
      this.statusType = 2;
    if (this.koStatus.indexOf(this.status) >= 0)
      this.statusType = 3;
  }

}
