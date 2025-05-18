import { Component, Input, OnInit } from '@angular/core';
import { formatDateForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { IncidentReport } from 'src/app/modules/reporting/model/IncidentReport';
import { IncidentReportService } from 'src/app/modules/reporting/services/incident.report.service';
import { CustomerOrder } from '../../model/CustomerOrder';

@Component({
  selector: 'incident-report-list',
  templateUrl: './incident-report-list.component.html',
  styleUrls: ['./incident-report-list.component.css']
})
export class IncidentReportListComponent implements OnInit {

  @Input() customerOrder: CustomerOrder | undefined;
  @Input() askForNewCri: boolean = false;
  incidents: IncidentReport[] | undefined;
  selectedIncident: IncidentReport | undefined;
  idRowSelected: number | undefined;
  displayedColumns: SortTableColumn<IncidentReport>[] = [];

  constructor(
    private incidentReportService: IncidentReportService,
  ) { }

  ngOnInit() {
    if (this.customerOrder)
      this.incidentReportService.getIncidentReportsForCustomerOrder(this.customerOrder.id).subscribe(reponse => {
        this.incidents = reponse;

        if (this.askForNewCri) {
          let incident = {} as IncidentReport;
          this.incidents.push(incident);
          this.selectEntity(incident);
        }
      })

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant" } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "title", fieldName: "title", label: "Titre" } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "incidentReportStatus", fieldName: "incidentReportStatus.label", label: "Status" } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "incidentResponsibility", fieldName: "incidentResponsibility.label", label: "Responsabilité" } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "initiatedBy", fieldName: "initiatedBy", label: "Initié par", displayAsEmployee: true } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "assignedTo", fieldName: "assignedTo", label: "Assigné à", displayAsEmployee: true } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "startDate", fieldName: "startDate", label: "Début", valueFonction: formatDateForSortTable } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "endDate", fieldName: "endDate", label: "Fin", valueFonction: formatDateForSortTable } as SortTableColumn<IncidentReport>);
  }

  selectEntity(element: IncidentReport) {
    this.selectedIncident = element;
    this.idRowSelected = element.id;
  }
}
