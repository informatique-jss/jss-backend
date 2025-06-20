import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Voucher } from '../../model/Voucher';
import { VoucherService } from '../../services/voucher.service';
import { NewVoucherDialogComponent } from '../new-voucher-dialog/new-voucher-dialog.component';

@Component({
  selector: 'voucher',
  templateUrl: './voucher.component.html',
  styleUrls: ['./voucher.component.css']
})
export class VoucherComponent implements OnInit {
  vouchers: Voucher[] | undefined;
  selectedVoucher: Voucher | undefined;
  displayedColumnsVoucher: SortTableColumn<Voucher>[] = [];
  tableActionVoucher: SortTableAction<Voucher>[] = [];

  constructor(private voucherService: VoucherService,
    private formBuilder: FormBuilder,
    public confirmationDialog: MatDialog,
    public newVoucherDialog: MatDialog
  ) { }

  ngOnInit() {
    this.getAllVouchers();

    this.displayedColumnsVoucher = [];
    this.displayedColumnsVoucher.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<Voucher>);
    this.displayedColumnsVoucher.push({ id: "code", fieldName: "code", label: "Code coupon" } as SortTableColumn<Voucher>);
    this.displayedColumnsVoucher.push({ id: "discountAmount", fieldName: "discountAmount", label: "Montant de la réduction" } as SortTableColumn<Voucher>);
    this.displayedColumnsVoucher.push({ id: "discountRate", fieldName: "discountRate", label: "Taux de réduction" } as SortTableColumn<Voucher>);
    this.displayedColumnsVoucher.push({ id: "startDate", fieldName: "startDate", label: "Début de validité" } as SortTableColumn<Voucher>);
    this.displayedColumnsVoucher.push({ id: "endDate", fieldName: "endDate", label: "Fin de validité" } as SortTableColumn<Voucher>);

    this.tableActionVoucher.push({
      actionIcon: "delete", actionName: "Supprimer le coupon", actionClick: (column: SortTableAction<Voucher>, element: Voucher, event: any) => {
        if (element) {
          const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
            maxWidth: "400px",
            data: {
              title: "Supprimer le coupon",
              content: "Êtes-vous sûr de vouloir continuer ?",
              closeActionText: "Annuler",
              validationActionText: "Supprimer"
            }
          });

          dialogRef.afterClosed().subscribe(dialogResult => {
            if (dialogResult)
              this.deleteVoucher(element);
          });
        }
      }, display: true,
    } as SortTableAction<Voucher>);
  }

  vouchersForm = this.formBuilder.group({
  });

  createVoucher() {
    const dialogRef = this.newVoucherDialog.open(NewVoucherDialogComponent, {
      maxWidth: "400px",
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult)
        this.voucherService.addOrUpdateVoucher(dialogResult).subscribe(response => {
          if (response)
            this.getAllVouchers();
        });
    });
  }

  getAllVouchers() {
    this.voucherService.getAllVouchers().subscribe(response => {
      if (response)
        this.vouchers = response;
    });
  }

  deleteVoucher(voucher: Voucher) {
    if (voucher)
      this.voucherService.deleteVoucher(voucher).subscribe(response => {
        if (response)
          this.getAllVouchers();
      });
  }
}
