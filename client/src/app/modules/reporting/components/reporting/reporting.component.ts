import { Component, ElementRef, Inject, Input, OnInit, SimpleChanges } from '@angular/core';

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

  @Input() dataToDisplay: any | undefined;
  @Input() settings: string | undefined;
  @Input() reportName: string = "report";

  constructor(@Inject(ElementRef) el: ElementRef) {
    this.el = el;
  }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.dataToDisplay || changes.settings) {
      this.data = [];
      if (this.dataToDisplay) {
        this.data = this.translateDataLabel(this.dataToDisplay);
        this.loadPivot();
      }
    }
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

  loadPivot() {
    if (this.data && this.data.length > 0) {
      let options: any = {};
      let restoreSettings = false;

      if (this.settings) {
        options = JSON.parse(this.settings);
        restoreSettings = true;
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

      options['menuLimit'] = 50000;
      options['renderers'] = renderersFr;
      options['rendererOptions'] = {
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

      this.targetElement.pivotUI(this.data, options, restoreSettings, "fr");
    }
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
