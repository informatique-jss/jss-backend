import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { Subject } from 'rxjs';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { DebourDel } from '../../model/DebourDel';
import { Provision } from '../../model/Provision';
import { DebourDelService } from '../../services/debour.del.service';

@Component({
  selector: 'debour',
  templateUrl: './debour.component.html',
  styleUrls: ['./debour.component.css']
})
export class DebourComponent implements OnInit {

  @Input() provision: Provision | undefined;
  displayedColumns: SortTableColumn<DebourDel>[] = [];
  refreshTable: Subject<void> = new Subject<void>();
  debours: DebourDel[] | undefined;

  constructor(
    private debourDelService: DebourDelService
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<DebourDel>);
    this.displayedColumns.push({ id: "billingType", fieldName: "billingType.label", label: "Débour" } as SortTableColumn<DebourDel>);
    this.displayedColumns.push({ id: "competentAuthority", fieldName: "competentAuthority.label", label: "Autorité compétente" } as SortTableColumn<DebourDel>);
    this.displayedColumns.push({ id: "debourAmount", fieldName: "debourAmount", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn<DebourDel>);
    this.displayedColumns.push({ id: "invoicedAmount", fieldName: "invoicedAmount", label: "Montant facturé TTC", valueFonction: formatEurosForSortTable } as SortTableColumn<DebourDel>);
    this.displayedColumns.push({ id: "paymentType", fieldName: "paymentType.label", label: "Type de paiement" } as SortTableColumn<DebourDel>);
    this.displayedColumns.push({ id: "paymentDateTime", fieldName: "paymentDateTime", label: "Date de paiement", valueFonction: formatDateForSortTable } as SortTableColumn<DebourDel>);
    this.displayedColumns.push({ id: "checkNumber", fieldName: "checkNumber", label: "N° de chèque" } as SortTableColumn<DebourDel>);
    this.displayedColumns.push({ id: "comments", fieldName: "comments", label: "Commentaires", isShrinkColumn: true } as SortTableColumn<DebourDel>);

    this.refreshTable.next();
    this.setData();

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.provision) {
      this.setData();
    }
  }

  setData() {
    if (this.provision) {
      this.debourDelService.getDebourByProvision(this.provision).subscribe(debours => {
        this.debours = debours;
        this.refreshTable.next();
      })
    }
  }
}
