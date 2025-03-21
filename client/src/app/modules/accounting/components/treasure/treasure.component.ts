import { Component, OnInit } from '@angular/core';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { TreasureResult } from '../../model/TreasureResult';
import { TreasureResultService } from '../../services/treasure.result.service';

@Component({
  selector: 'treasure',
  templateUrl: './treasure.component.html',
  styleUrls: ['./treasure.component.css']
})
export class TreasureComponent implements OnInit {
  displayedColumns: SortTableColumn<TreasureResult>[] = [];
  treasureResults: TreasureResult[] = [];

  constructor(private treasureResultService: TreasureResultService,
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "month", fieldName: "month", label: "Mois" } as SortTableColumn<TreasureResult>);
    this.displayedColumns.push({ id: "cdnAmount", fieldName: "cdnAmount", label: "CDN", valueFonction: formatEurosForSortTable } as SortTableColumn<TreasureResult>);
    this.displayedColumns.push({ id: "bnpAmount", fieldName: "bnpAmount", label: "BNP", valueFonction: formatEurosForSortTable } as SortTableColumn<TreasureResult>);
    this.displayedColumns.push({ id: "sicavAmount", fieldName: "sicavAmount", label: "Sicav", valueFonction: formatEurosForSortTable } as SortTableColumn<TreasureResult>);
    this.displayedColumns.push({ id: "termeAmount", fieldName: "termeAmount", label: "Comptes à terme", valueFonction: formatEurosForSortTable } as SortTableColumn<TreasureResult>);
    this.displayedColumns.push({ id: "totalAmount", fieldName: "totalAmount", label: "Total", valueFonction: formatEurosForSortTable } as SortTableColumn<TreasureResult>);

    this.refresh();
  }

  refresh() {
    this.treasureResultService.getTreasure().subscribe(response => {
      this.treasureResults = response;
      let treasureTotal = { month: "Total", cdnAmount: 0, bnpAmount: 0, sicavAmount: 0, termeAmount: 0, totalAmount: 0 } as TreasureResult;
      if (response) {
        for (let result of response) {
          treasureTotal.cdnAmount += result.cdnAmount;
          treasureTotal.bnpAmount += result.bnpAmount;
          treasureTotal.sicavAmount += result.sicavAmount;
          treasureTotal.termeAmount += result.termeAmount;
          treasureTotal.totalAmount += result.totalAmount;
        }
        this.treasureResults.push(treasureTotal);
      }
    })
  }
}

