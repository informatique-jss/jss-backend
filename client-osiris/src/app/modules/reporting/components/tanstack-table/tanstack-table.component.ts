import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit, signal } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { Cell, ColumnDef, createAngularTable, FlexRenderDirective, getCoreRowModel, getFilteredRowModel, getPaginationRowModel, getSortedRowModel, Table } from '@tanstack/angular-table';
import { LucideAngularModule, LucideDownload, LucideSearch } from 'lucide-angular';
import * as XLSX from "xlsx";
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { TanstackTablePaginationComponent } from '../tanstack-table-pagination/tanstack-table-pagination.component';

@Component({
  selector: 'tanstack-table',
  imports: [...SHARED_IMPORTS, FlexRenderDirective, LucideAngularModule, CommonModule, NgIcon, TanstackTablePaginationComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './tanstack-table.component.html',
  styleUrls: ['./tanstack-table.component.css'],
  standalone: true
})
export class TanstackTableComponent<T> implements OnInit {
  @Input() columns!: ColumnDef<any>[];
  @Input() data!: any[];
  @Input() title: string | undefined;
  @Input() className = 'card';
  emptyMessage: string | undefined = 'Aucun élément trouvé';
  @Input() showHeaders = true;
  table!: Table<T>;
  searchValue = '';

  LucideDownload = LucideDownload;
  LucideSearch = LucideSearch;
  Math = Math;

  selectedColumn: string | 'All' = 'All';

  ngOnInit(): void {
    let dataInput = signal<any[]>(this.data);
    this.table = createAngularTable<any>(() => ({
      data: dataInput(),
      columns: this.columns,
      getCoreRowModel: getCoreRowModel(),
      getPaginationRowModel: getPaginationRowModel(),
      getFilteredRowModel: getFilteredRowModel(),
      getSortedRowModel: getSortedRowModel(),
      globalFilterFn: 'includesString',
      initialState: {
        pagination: {
          pageSize: 5
        }
      }
    }));
  }

  getId(cell: Cell<any, unknown>): number {
    return cell.row.original.id;
  }

  setPageSize(size: number) {
    this.table.setPageSize(size);
  }

  exportToExcel(): void {
    if (!this.table) return;
    const rowModel = this.table.getPrePaginationRowModel().rows;
    const headers = this.table.getAllLeafColumns().map(c => c.columnDef.header as string);
    const data = rowModel.map(row =>
      row.getVisibleCells().map(cell => cell.getValue())
    );
    const worksheet = XLSX.utils.aoa_to_sheet([headers, ...data]);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Export");
    const excelBuffer = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
    const blob = new Blob([excelBuffer], { type: "application/octet-stream" });
    this.saveAs(this.title ? this.title : "export.xlsx", blob);
  }

  saveAs(filename: string, blob: any) {
    let downloadLink = document.createElement('a');
    downloadLink.href = window.URL.createObjectURL(blob);
    downloadLink.setAttribute('download', filename);
    document.body.appendChild(downloadLink);
    downloadLink.click();
  }

  applyExternalFilter(value: string) {
    this.searchValue = value;
    this.table.setGlobalFilter(value);
  }

}
