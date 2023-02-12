import { Component, ElementRef, Inject, OnInit } from '@angular/core';
import { QuotationReportingService } from '../../services/quotation.reporting.service';

import 'pivottable/dist/pivot.css';
import 'pivottable/dist/pivot.min.js';
import { QuotationReporting } from '../../model/QuotationReporting';
declare var jQuery: any;
declare var $: any;
declare var google: any;

@Component({
  selector: 'app-reporting',
  templateUrl: './reporting.component.html',
  styleUrls: ['./reporting.component.css']
})
export class ReportingComponent implements OnInit {
  private el: ElementRef;
  data: QuotationReporting[] | undefined;
  targetElement: any | undefined;
  constructor(@Inject(ElementRef) el: ElementRef,
    private quotationReportingService: QuotationReportingService) {
    this.el = el;
  }

  ngOnInit() {
    this.quotationReportingService.getQuotationReporting().subscribe(response => {
      this.data = response;
      this.loadPivot();
    })
  }

  ngAfterViewInit() {

    if (!this.el ||
      !this.el.nativeElement ||
      !this.el.nativeElement.children) {
      console.log('cant build without element');
      return;
    }

    var container = this.el.nativeElement;
    var inst = jQuery(container);
    this.targetElement = inst.find('#output');

    if (!this.targetElement) {
      console.log('cant find the pivot element');
      return;
    }

    //this helps if you build more than once as it will wipe the dom for that element
    while (this.targetElement.firstChild) {
      this.targetElement.removeChild(this.targetElement.firstChild);
    }

    //here is the magic
    google.load("visualization", "1", { packages: ["corechart", "charteditor"] });
  }

  loadPivot() {
    if (this.data) {
      var renderers = $.extend($.pivotUtilities.renderers,
        $.pivotUtilities.gchart_renderers);
      this.targetElement.pivotUI(this.data, {
        renderers: renderers,
      }, false, "fr");
    }
  }
}
