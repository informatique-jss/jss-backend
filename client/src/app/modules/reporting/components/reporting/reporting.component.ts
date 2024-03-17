import { Component, ElementRef, EventEmitter, Inject, Input, OnInit, Output, SimpleChanges } from '@angular/core';

//import 'pivottable/dist/pivot.css';
import 'pivottable/dist/pivot.min.js';
import { Dictionnary } from '../../../../libs/Dictionnary';
declare var jQuery: any;
declare var $: any;

@Component({
  selector: 'reporting',
  templateUrl: './reporting.component.html',
  styleUrls: ['./reporting.component.css']
})
export class ReportingComponent implements OnInit {
  private el: ElementRef;
  data: any | undefined;
  targetElement: any | undefined;

  @Input() settings: string | undefined;
  @Input() reportName: string = "report";
  @Output() columnsChange: EventEmitter<string[]> = new EventEmitter<string[]>();
  options: any;
  restoreSettings = false;

  currentColumns: string[] = [];

  constructor(@Inject(ElementRef) el: ElementRef) {
    this.el = el;
  }

  ngOnInit() {
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
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.settings)
      this.loadPivot();
  }

  refreshPivotWithData(data: any) {
    if (!this.options)
      this.loadPivot();
    this.data = [];
    if (data) {
      this.data = this.translateDataLabel(data);
      this.refreshPivot();
      this.updateCurrentColumns();
    }
  }

  loadPivot() {
    this.options = {};

    if (this.settings) {
      this.options = JSON.parse(this.settings);
      this.restoreSettings = true;
    }
    var renderersEn = $.extend($.pivotUtilities.renderers,
      $.pivotUtilities.plotly_renderers);
    var renderersFr: any = [];
    renderersFr["Tableau"] = renderersEn["Table"];
    renderersFr["Tableau avec histogramme"] = renderersEn["Table Barchart"];
    renderersFr["Carte de chaleur"] = renderersEn["Heatmap"];
    renderersFr["Carte de chaleur en lignes"] = renderersEn["Row Heatmap"];
    renderersFr["Carte de chaleur en colonnes"] = renderersEn["Col Heatmap"];
    renderersFr["Histogramme"] = renderersEn["Bar Chart"];
    renderersFr["Histogramme empilé"] = renderersEn["Stacked Bar Chart"];
    renderersFr["Histogramme horizontal"] = renderersEn["Horizontal Bar Chart"];
    renderersFr["Histogramme horizontal empilé"] = renderersEn["Horizontal Stacked Bar Chart"];
    renderersFr["Courbe"] = renderersEn["Line Chart"];
    renderersFr["Graphique en aires"] = renderersEn["Area Chart"];
    renderersFr["Nuage de points"] = renderersEn["Scatter Chart"];
    renderersFr["Diagrammes circulaires multiples"] = renderersEn["Multiple Pie Chart"];

    this.options['menuLimit'] = 50000;
    this.options['renderers'] = renderersFr;
    this.options['rendererOptions'] = {
      plotly: {
        width: 900,
        height: 600,
      },
      plotlyConfig: {
        locale: 'fr',
        editable: "true",
        showLink: true,
        plotlyServerURL: "https://chart-studio.plotly.com",
        linkText: 'Editer',
        displaylogo: false
      }
    };

    this.options['onRefresh'] = (config: any) => {
      if (this.updateCurrentColumns()) {
        let untranslateColumns: string[] = [];
        if (this.currentColumns)
          for (let column of this.currentColumns)
            untranslateColumns.push(this.untranslateDataLabel(column));
        this.columnsChange.emit(untranslateColumns);
      }
    }
    this.restoreSettings = false;
  }

  refreshPivot() {
    if (this.targetElement)
      this.targetElement.pivotUI(this.data, this.options, this.restoreSettings, "fr");
  }

  updateCurrentColumns(): boolean {
    let hasChanged = false;
    let settings = $("#output").data("pivotUIOptions");
    if (settings) {
      let settingsCopy = JSON.parse(JSON.stringify(settings));
      let foundColumns = [];
      if (settingsCopy.rows) {
        for (let row of settingsCopy.rows) {
          if (foundColumns.indexOf(row) < 0) {
            foundColumns.push(row);
          }
        }
      }
      if (settingsCopy.cols) {
        for (let col of settingsCopy.cols) {
          if (foundColumns.indexOf(col) < 0) {
            foundColumns.push(col);
          }
        }
      }
      if (settingsCopy.vals) {
        for (let val of settingsCopy.vals) {
          if (foundColumns.indexOf(val) < 0) {
            foundColumns.push(val);
          }
        }
      }

      if (foundColumns && foundColumns.length > 0 && this.currentColumns && this.currentColumns.length == 0)
        hasChanged = true;
      else if (this.currentColumns && foundColumns)
        for (let current of this.currentColumns)
          for (let found of foundColumns)
            if (this.currentColumns.indexOf(found) < 0 || foundColumns.indexOf(current) < 0)
              hasChanged = true;
      this.currentColumns = foundColumns;
    }
    return hasChanged;
  }

  translateDataLabel(data: any[]): any[] {
    let outData: any[] = [];
    let dictionnary = Dictionnary as any;
    if (data) {
      for (let item of data) {
        let outItem: any = {} as any;
        for (let key in item) {
          if (dictionnary[key])
            outItem[dictionnary[key]] = item[key];
          else
            outItem[key] = item[key];
        }
        outData.push(outItem);
      }
    }
    return outData;
  }

  untranslateDataLabel(label: string): string {
    let dictionnary = Dictionnary as any;
    if (label) {
      for (let key in dictionnary) {
        if (dictionnary[key] == label)
          return key;
      }
    }
    return '';
  }

  getCurrentSettings() {
    var settings = $("#output").data("pivotUIOptions");
    var settingsCopy = JSON.parse(JSON.stringify(settings));
    //delete some values which will not serialize to JSON
    delete settingsCopy["aggregators"];
    delete settingsCopy["renderers"];
    delete settingsCopy["rendererOptions"];
    delete settingsCopy["localeStrings"];
    return JSON.stringify(settingsCopy);
  }

  exportToExcel() {
    var uri = 'data:application/vnd.ms-excel;base64,'
      , template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--><meta http-equiv="content-type" content="text/plain; charset=UTF-8"/></head><body><table>{table}</table></body></html>'
      , base64 = function (s: any) { return window.btoa(unescape(encodeURIComponent(s))) }
      , format = function (s: any, c: any) { return s.replace(/{(\w+)}/g, function (m: any, p: any) { return c[p]; }) }

    let table = document.getElementsByClassName("pvtTable")[0];
    var ctx = { worksheet: 'Export' || 'Worksheet', table: table.innerHTML }

    // Construct the <a> element
    var link = document.createElement("a");
    link.download = this.reportName + '.xls';
    link.href = uri + base64(format(template, ctx));

    document.body.appendChild(link);
    link.click();

    // Cleanup the DOM
    document.body.removeChild(link);
  }

  displayExportButton() {
    let a = $('.pvtRenderer  option:selected')[0];
    if (a && a.text && (a.text == 'Tableau' || a.text.indexOf('Carte de chaleur') >= 0))
      return true;
    return false;
  }
}
