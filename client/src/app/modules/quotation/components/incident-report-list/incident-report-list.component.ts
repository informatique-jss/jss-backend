import { Component, Input, OnInit } from '@angular/core';
import { formatDateForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { IncidentReport } from 'src/app/modules/reporting/model/IncidentReport';
import { IncidentReportService } from 'src/app/modules/reporting/services/incident.report.service';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { CustomerOrder } from '../../model/CustomerOrder';

@Component({
  selector: 'incident-report-list',
  templateUrl: './incident-report-list.component.html',
  styleUrls: ['./incident-report-list.component.css']
})
export class IncidentReportListComponent implements OnInit {

  @Input() customerOrder: CustomerOrder | undefined;
  @Input() tiers: Tiers | undefined;
  @Input() responsable: Responsable | undefined;
  @Input() askForNewCri: boolean = false;
  incidents: IncidentReport[] | undefined;
  selectedIncident: IncidentReport | undefined;
  idRowSelected: number | undefined;
  displayedColumns: SortTableColumn<IncidentReport>[] = [];

  constructor(
    private incidentReportService: IncidentReportService,
    private habilitationService: HabilitationsService
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
    else if (this.tiers)
      this.incidentReportService.getIncidentReportsForTiers(this.tiers.id).subscribe(response => {
        this.incidents = response;
      })
    else if (this.responsable)
      this.incidentReportService.getIncidentReportsForReponsable(this.responsable.id).subscribe(response => {
        this.incidents = response;
      })

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "Identifiant" } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "title", fieldName: "title", label: "Titre" } as SortTableColumn<IncidentReport>);
    if (this.tiers || this.responsable) {
      this.displayedColumns.push({ id: "customerOrder", fieldName: "customerOrder.id", label: "N° de commande", actionLinkFunction: (column: SortTableColumn<IncidentReport>, element: IncidentReport) => { return ['order', element.customerOrder.id]; }, actionIcon: "visibility", actionTooltip: "Voir la commande associée" } as SortTableColumn<IncidentReport>);
    }
    this.displayedColumns.push({ id: "incidentReportStatus", fieldName: "incidentReportStatus.label", label: "Status" } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "incidentResponsibility", fieldName: "incidentResponsibility.label", label: "Responsabilité" } as SortTableColumn<IncidentReport>);

    if (this.habilitationService.canUpdateIncidentResponsibility())
      this.displayedColumns.push({ id: "initiatedBy", fieldName: "initiatedBy", label: "Initié par", displayAsEmployee: true } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "assignedTo", fieldName: "assignedTo", label: "Assigné à", displayAsEmployee: true } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "startDate", fieldName: "startDate", label: "Début", valueFonction: formatDateForSortTable } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "endDate", fieldName: "endDate", label: "Fin", valueFonction: formatDateForSortTable } as SortTableColumn<IncidentReport>);
    this.displayedColumns.push({ id: "description", fieldName: "description", label: "Description", isShrinkColumn: true } as SortTableColumn<IncidentReport>);
  }

  selectEntity(element: IncidentReport) {
    this.selectedIncident = element;
    this.idRowSelected = element.id;
  }
}


