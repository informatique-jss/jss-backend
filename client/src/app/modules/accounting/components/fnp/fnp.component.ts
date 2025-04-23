import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { FnpResult } from '../../model/FnpResult';
import { FnpResultService } from '../../services/fnp.result.service';

@Component({
  selector: 'fnp',
  templateUrl: './fnp.component.html',
  styleUrls: ['./fnp.component.css']
})
export class FnpComponent implements OnInit {
  fnpForm = this.formBuilder.group({});
  displayedColumns: SortTableColumn<FnpResult>[] = [];
  accountingDate: Date = new Date();
  fnpResults: FnpResult[] = [];

  constructor(private fnpResultService: FnpResultService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "customerOrder", fieldName: "customerOrder", label: "Tiers" } as SortTableColumn<FnpResult>);
    this.displayedColumns.push({ id: "amount", fieldName: "amount", label: "Montant HT (en €)", valueFonction: formatEurosForSortTable } as SortTableColumn<FnpResult>);
    this.displayedColumns.push({ id: "isPayed", fieldName: "isPayed", label: "Payé ?", valueFonction: (element: FnpResult, column: SortTableColumn<FnpResult>) => { return element.isPayed ? "Oui" : "Non" } } as SortTableColumn<FnpResult>);

    this.refresh();
  }

  refresh() {
    if (this.accountingDate) {
      this.accountingDate = new Date(this.accountingDate.setHours(12));
      this.fnpResultService.getFnp(this.accountingDate).subscribe(response => this.fnpResults = response)
    }
  }

}

