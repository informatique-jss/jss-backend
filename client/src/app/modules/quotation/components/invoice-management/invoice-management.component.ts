import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { Vat } from 'src/app/modules/miscellaneous/model/Vat';
import { Affaire } from '../../model/Affaire';
import { IQuotation } from '../../model/IQuotation';
import { ProvisionType } from '../../model/ProvisionType';
import { QuotationComponent } from '../quotation/quotation.component';

@Component({
  selector: 'invoice-management',
  templateUrl: './invoice-management.component.html',
  styleUrls: ['./invoice-management.component.css']
})
export class InvoiceManagementComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() quotation: IQuotation = {} as IQuotation;
  @Input() editMode: boolean = false;
  @Output() invoiceItemChange: EventEmitter<void> = new EventEmitter<void>();

  constructor(private formBuilder: FormBuilder,) { }

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

  getVatTotals(): Vat[] {
    return QuotationComponent.computeVatTotals(this.quotation);
  }

  getPriceTotal(): number {
    return QuotationComponent.computePriceTotal(this.quotation);
  }

  getListOfAffaires(): Affaire[] {
    return QuotationComponent.computeListOfAffaires(this.quotation);
  }

  isFirstProvisionTypeForAffaire(provisionType: ProvisionType, affaire: Affaire, index: number): boolean {
    return QuotationComponent.computeIsFirstProvisionTypeForAffaire(provisionType, affaire, index, this.quotation);
  }

}
