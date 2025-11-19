import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, HostListener, Input, OnInit, signal, SimpleChanges, ViewChild } from '@angular/core';
import { NgbDropdown, NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { Cell, Column, ColumnDef, createAngularTable, FlexRenderDirective, getCoreRowModel, getFilteredRowModel, getPaginationRowModel, getSortedRowModel, Row, Table } from '@tanstack/angular-table';
import { LucideAngularModule, LucideDownload, LucideSearch, LucideSettings } from 'lucide-angular';
import * as XLSX from "xlsx";
import { GenericTableAction } from '../generic-list/GenericTableAction';
import { SHARED_IMPORTS } from '../SharedImports';
import { TanstackTablePaginationComponent } from '../tanstack-table-pagination/tanstack-table-pagination.component';
import { IId } from './Iid';

@Component({
  selector: 'tanstack-table',
  imports: [...SHARED_IMPORTS, FlexRenderDirective, LucideAngularModule, CommonModule, NgIcon, TanstackTablePaginationComponent, NgbDropdownModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './tanstack-table.component.html',
  styleUrls: ['./tanstack-table.component.css'],
  standalone: true
})
export class TanstackTableComponent<T extends IId> implements OnInit {
  @ViewChild('contextMenuDropdown') contextMenuDropdown?: NgbDropdown;
  @ViewChild('contextMenu') contextMenu?: ElementRef<HTMLDivElement>;

  @Input() columns!: ColumnDef<T>[];
  @Input() actions!: GenericTableAction<T>[];
  @Input() data!: T[];
  dataInput = signal<T[]>(this.data);
  @Input() title: string | undefined;
  @Input() className = 'card';
  emptyMessage: string | undefined = 'Aucun élément trouvé';
  @Input() showHeaders = true;
  table!: Table<T>;
  searchValue = '';
  filterCategory = '';
  LucideDownload = LucideDownload;
  LucideSearch = LucideSearch;
  LucideSettings = LucideSettings;
  Math = Math;
  isMenuVisible = false;

  contextMenuPosition = { top: '0px', left: '0px' };
  adaptedPageSize = 15;

  constructor(
    private el: ElementRef,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.table = createAngularTable<T>(() => ({
      data: this.dataInput(),
      columns: this.columns,
      getCoreRowModel: getCoreRowModel(),
      getPaginationRowModel: getPaginationRowModel(),
      getFilteredRowModel: getFilteredRowModel(),
      getSortedRowModel: getSortedRowModel(),
      globalFilterFn: 'includesString',
      initialState: {
        pagination: {
          pageSize: 15
        }
      }
    }));
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['data']) {
      this.dataInput.set(this.data);
    }
  }

  ngAfterViewInit() {
    const viewportHeightPx = window.innerHeight;
    let offset = 5;
    if (viewportHeightPx > 1100)
      offset = 1;
    else if (viewportHeightPx > 940)
      offset = 2;
    else if (viewportHeightPx > 750)
      offset = 3;
    else if (viewportHeightPx > 600)
      offset = 4;

    this.adaptedPageSize = Math.ceil(viewportHeightPx / 60) - offset;
    this.table.setPageSize(this.adaptedPageSize);
  }

  getId(cell: Cell<T, unknown>): number {
    return cell.row.original.id;
  }

  setPageSize(size: number) {
    this.table.setPageSize(size);
  }

  onToggleAllPageRows(checked: boolean) {
    this.table.toggleAllPageRowsSelected(checked);
    this.cdr.detectChanges();
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

  doubleClickOnCell(row: any, column: Column<any>) {
    if (column && column.columnDef && column.columnDef.meta && (column.columnDef.meta as any).eventOnDoubleClick)
      (column.columnDef.meta as any).eventOnDoubleClick.next(row)
  }

  clickOnAction(action: GenericTableAction<T>) {
    if (action && action.eventOnClick)
      action.eventOnClick.next(this.table.getSelectedRowModel().rows);
  }

  onRightClick(event: MouseEvent, row: Row<T>) {
    event.preventDefault();
    this.isMenuVisible = true;

    if (this.table.getSelectedRowModel().rows.length == 1) {
      this.table.getSelectedRowModel().rows[0].toggleSelected(false);
      this.cdr.detectChanges();
    }
    row.toggleSelected(true);

    this.contextMenuPosition = {
      top: `${event.clientY}px`,
      left: `${event.clientX}px`,
    };

    this.contextMenuDropdown?.open();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!(event.target as HTMLElement).closest('[ngbDropdown]')) {
      this.contextMenuDropdown?.close();
      this.isMenuVisible = false;
    }
  }

  isActionDisabled(action: GenericTableAction<T>) {
    let display = undefined;
    if (action.minNumberOfElementsRequiredToDisplay) {
      if (this.table.getSelectedRowModel().rows.length >= action.minNumberOfElementsRequiredToDisplay)
        display = true;
      else
        display = false;
    }
    if ((display || display == undefined) && action.maxNumberOfElementsRequiredToDisplay) {
      if (this.table.getSelectedRowModel().rows.length <= action.maxNumberOfElementsRequiredToDisplay)
        display = true;
      else
        display = false;
    }
    if (display == undefined)
      return false;
    return !display;
  }

  isDisplayActionMenu() {
    if (this.actions)
      for (let action of this.actions)
        if (action.isDisplayOutOfMenu == undefined || action.isDisplayOutOfMenu == false)
          return true;
    return false;
  }

}
