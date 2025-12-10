import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { SHARED_IMPORTS } from '../SharedImports';

@Component({
  selector: 'tanstack-table-pagination',
  templateUrl: './tanstack-table-pagination.component.html',
  styleUrls: ['./tanstack-table-pagination.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS, NgIcon]
})
export class TanstackTablePaginationComponent implements OnInit {

  @Input() totalItems!: number;
  @Input() start!: number;
  @Input() end!: number;
  @Input() showInfo: boolean = false;

  @Input() canPreviousPage: boolean = false;
  @Input() pageCount!: number;
  @Input() pageIndex!: number;
  @Input() canNextPage: boolean = false;

  @Output() previousPage = new EventEmitter<void>();
  @Output() nextPage = new EventEmitter<void>();
  @Output() setPageIndex = new EventEmitter<number>();

  get pages(): number[] {
    return Array(this.pageCount).fill(0);
  }

  constructor() { }

  ngOnInit() {
  }

}
