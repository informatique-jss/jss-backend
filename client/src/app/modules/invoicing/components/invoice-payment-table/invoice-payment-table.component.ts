import { Component, Input, OnInit } from '@angular/core';
import { formatDate } from 'src/app/libs/FormatHelper';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { getAmountPayed, getRemainingToPay } from '../invoice-tools';

@Component({
  selector: 'invoice-payment-table',
  templateUrl: './invoice-payment-table.component.html',
  styleUrls: ['./invoice-payment-table.component.css']
})
export class InvoicePaymentTableComponent implements OnInit {

  @Input() invoice: Invoice = {} as Invoice;

  formatDate = formatDate;
  getAmountRemaining = getRemainingToPay;
  getAmountPayed = getAmountPayed;

  constructor() { }

  ngOnInit() {
  }



}
