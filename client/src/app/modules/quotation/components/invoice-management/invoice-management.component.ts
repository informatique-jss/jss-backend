import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { instanceOfCustomerOrder, instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { InvoiceService } from 'src/app/modules/invoicing/services/invoice.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from '../../../../services/app.service';
import { CustomerOrder } from '../../model/CustomerOrder';
import { IQuotation } from '../../model/IQuotation';
import { VatBase } from '../../model/VatBase';
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


  instanceOfCustomerOrderFn = instanceOfCustomerOrder;
  instanceOfQuotation = instanceOfQuotation;

  invoiceStatusCancelled = this.constantService.getInvoiceStatusCancelled();

  constructor(private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private appService: AppService,
    protected invoiceService: InvoiceService,) { }

  ngOnInit() {
    this.invoiceManagementForm.markAllAsTouched();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation != undefined) {
      this.invoiceManagementForm.markAllAsTouched();
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

  getApplicableVat(): VatBase[] {
    return QuotationComponent.computeApplicableVat(this.quotation);
  }

  getPriceTotal(): number {
    return QuotationComponent.computePriceTotal(this.quotation);
  }

  getRemainingToPay() {
    if (instanceOfCustomerOrder(this.quotation))
      return Math.round((QuotationComponent.computePriceTotal(this.quotation) - QuotationComponent.computePayed(this.quotation)) * 100) / 100;
    return this.getPriceTotal();
  }

  canModifyInvoice(): boolean {
    if (instanceOfQuotation(this.quotation))
      return true;
    if (instanceOfCustomerOrder(this.quotation)) {
      if ((this.quotation as CustomerOrder).invoices == undefined || (this.quotation as CustomerOrder).invoices == null || (this.quotation as CustomerOrder).invoices.length == 0) {
        return true;
      } else {
        for (let invoice of (this.quotation as CustomerOrder).invoices) {
          if (invoice.invoiceStatus.id != this.invoiceStatusCancelled.id)
            return true;
        }
      }
    }
    return false;
  }

  openRoute(event: any, link: string) {
    this.appService.openRoute(event, link, null);
  }

}
