import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { FaeResult } from '../../model/FaeResult';
import { FaeResultService } from '../../services/fae.result.service';

@Component({
  selector: 'fae',
  templateUrl: './fae.component.html',
  styleUrls: ['./fae.component.css']
})
export class FaeComponent implements OnInit {
  faeForm = this.formBuilder.group({});
  displayedColumns: SortTableColumn<FaeResult>[] = [];
  accountingDate: Date = new Date();
  faeResults: FaeResult[] = [];

  constructor(private faeResultService: FaeResultService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "vat", fieldName: "vat", label: "TVA" } as SortTableColumn<FaeResult>);
    this.displayedColumns.push({ id: "affaire", fieldName: "affaire", label: "Affaire" } as SortTableColumn<FaeResult>);
    this.displayedColumns.push({ id: "customerOrderId", fieldName: "customerOrderId", label: "Commande" } as SortTableColumn<FaeResult>);
    this.displayedColumns.push({ id: "amount", fieldName: "amount", label: "Montant HT (en €)", valueFonction: formatEurosForSortTable } as SortTableColumn<FaeResult>);

    this.refresh();
  }

  refresh() {
    if (this.accountingDate) {
      this.accountingDate = new Date(this.accountingDate.setHours(12));
      this.faeResultService.getFae(this.accountingDate).subscribe(response => this.faeResults = response)
    }
  }

}
