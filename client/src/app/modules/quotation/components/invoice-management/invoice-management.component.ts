import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { InvoiceService } from 'src/app/modules/invoicing/services/invoice.service';
import { Vat } from 'src/app/modules/miscellaneous/model/Vat';
import { CustomerOrder } from '../../model/CustomerOrder';
import { Invoice } from '../../model/Invoice';
import { IQuotation } from '../../model/IQuotation';
import { QuotationComponent } from '../quotation/quotation.component';

@Component({
  selector: 'invoice-management',
  templateUrl: './invoice-management.component.html',
  styleUrls: ['./invoice-management.component.css']
})
export class InvoiceManagementComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() quotation: IQuotation = {} as IQuotation;
  customerOrder: CustomerOrder | undefined;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Output() invoiceItemChange: EventEmitter<void> = new EventEmitter<void>();


  invoice: Invoice = {} as Invoice;

  constructor(private formBuilder: FormBuilder,
    protected invoiceService: InvoiceService,) { }

  ngOnInit() {
    this.invoiceManagementForm.markAllAsTouched();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation != undefined) {
      this.invoiceManagementForm.markAllAsTouched();
      if (this.quotation && this.quotation.id)
        this.invoiceService.getInvoiceForCustomerOrder(this.quotation).subscribe(response => {
          this.invoice = response;
        })
    }

  }

  invoiceManagementForm = this.formBuilder.group({
  });

  itemChange() {
    this.invoiceItemChange.emit();
  }

  getFormStatus(): boolean {
    this.invoiceManagementForm.markAllAsTouched();
    return this.invoiceManagementForm.valid;
  }

  getPreTaxPriceTotal(): number {
    return QuotationComponent.computePreTaxPriceTotal(this.quotation);
  }

  getDiscountTotal(): number {
    return QuotationComponent.computeDiscountTotal(this.quotation);
  }

  getVatTotal(): number {
    return QuotationComponent.computeVatTotal(this.quotation);
  }

  getApplicableVat(): Vat | undefined {
    return QuotationComponent.computeApplicableVat(this.quotation);
  }

  getPriceTotal(): number {
    return QuotationComponent.computePriceTotal(this.quotation);
  }

}
