import { Component, Input, OnInit } from '@angular/core';
import { Voucher } from 'src/app/modules/crm/model/Voucher';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { CustomerOrderService } from '../../../quotation/services/customer.order.service';
import { Responsable } from '../../model/Responsable';
import { ResponsableService } from '../../services/responsable.service';

@Component({
  selector: 'vouchers-list',
  templateUrl: './vouchers-list.component.html',
  styleUrls: ['./vouchers-list.component.css']
})
export class VouchersListComponent implements OnInit {

  constructor(private responsableService: ResponsableService,
    private customerOrderService: CustomerOrderService
  ) { }

  @Input() responsableToDisplay: Responsable | undefined;
  displayedColumnsVoucher: SortTableColumn<Voucher>[] = [];
  displayedColumnsOrders: SortTableColumn<CustomerOrder>[] = [];
  responsableVouchers: Voucher[] = [];
  responsableOrders: CustomerOrder[] = [];

  ngOnInit() {
    if (this.responsableToDisplay && this.responsableToDisplay.vouchers && this.responsableToDisplay.vouchers.length > 0) {
      this.responsableVouchers = this.responsableToDisplay.vouchers;
      this.displayedColumnsVoucher = [];
      this.displayedColumnsVoucher.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<Voucher>);
      this.displayedColumnsVoucher.push({ id: "code", fieldName: "code", label: "Code coupon" } as SortTableColumn<Voucher>);
      this.displayedColumnsVoucher.push({ id: "discount", fieldName: "discountAmount", label: "Valeur de la réduction" } as SortTableColumn<Voucher>);
      this.displayedColumnsVoucher.push({ id: "startDate", fieldName: "startDate", label: "Début de validité" } as SortTableColumn<Voucher>);
      this.displayedColumnsVoucher.push({ id: "endDate", fieldName: "endDate", label: "Fin de validité" } as SortTableColumn<Voucher>);

      for (let voucher of this.responsableVouchers) {
        this.customerOrderService.getCustomerOrdersByVoucherAndResponsable(voucher, this.responsableToDisplay).subscribe(response => {
          if (response)
            for (let order of response)
              if (this.responsableOrders.indexOf(order) < 0)
                this.responsableOrders.push(order);
        });
      }

      this.displayedColumnsOrders = [];
      this.displayedColumnsOrders.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<CustomerOrder>);
      this.displayedColumnsOrders.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création" } as SortTableColumn<CustomerOrder>);

    }
  }

}
