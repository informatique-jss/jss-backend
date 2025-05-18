import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { INCIDENT_REPORT_TO_ANALYSE, INCIDENT_REPORT_TO_COMPLETE } from 'src/app/libs/Constants';
import { IncidentReport } from 'src/app/modules/reporting/model/IncidentReport';
import { IncidentReportStatus } from 'src/app/modules/reporting/model/IncidentReportStatus';
import { IncidentReportService } from 'src/app/modules/reporting/services/incident.report.service';
import { IncidentReportStatusService } from 'src/app/modules/reporting/services/incident.report.status.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { CustomerOrder } from '../../model/CustomerOrder';
import { Provision } from '../../model/Provision';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';

@Component({
  selector: 'add-incident-report',
  templateUrl: './add-incident-report.component.html',
  styleUrls: ['./add-incident-report.component.css']
})
export class AddIncidentReportComponent implements OnInit {

  @Input() incident: IncidentReport | undefined;
  INCIDENT_REPORT_TO_COMPLETE = INCIDENT_REPORT_TO_COMPLETE;
  INCIDENT_REPORT_TO_ANALYSE = INCIDENT_REPORT_TO_ANALYSE;
  incidentReportStatus: IncidentReportStatus[] | undefined;
  @Input() customerOrder: CustomerOrder | undefined;
  asso: AssoAffaireOrder[] | undefined;

  constructor(
    private incidentReportService: IncidentReportService,
    private formBuilder: FormBuilder,
    private habilitationService: HabilitationsService,
    private incidentReportStatusService: IncidentReportStatusService,
    private assoAffaireOrderService: AssoAffaireOrderService
  ) { }

  incidentForm = this.formBuilder.group({});

  ngOnInit() {
    this.incidentReportStatusService.getIncidentReportStatusList().subscribe(response => this.incidentReportStatus = response);
    if (this.customerOrder)
      this.assoAffaireOrderService.getAssoAffaireOrdersForCustomerOrder(this.customerOrder).subscribe(response => {
        this.asso = response;
      })
  }

  canUpdateIncidentResponsibility() {
    return this.habilitationService.canUpdateIncidentResponsibility();
  }

  saveIncident() {
    if (this.incident && this.incident.title && this.customerOrder) {
      this.incident.customerOrder = this.customerOrder;
      if (this.incident.startDate) {
        this.incident.startDate = new Date(this.incident.startDate);
        this.incident.startDate = new Date(this.incident.startDate.setHours(12));
      }
      if (this.incident.endDate) {
        this.incident.endDate = new Date(this.incident.endDate);
        this.incident.endDate = new Date(this.incident.endDate.setHours(12));
      }
      this.incidentReportService.addOrUpdateIncidentReport(this.incident).subscribe();
    }
  }

  markAsCompleted() {
    if (this.incident && this.incidentReportStatus) {
      this.incident.incidentReportStatus = this.incidentReportStatus.find(s => s.code == INCIDENT_REPORT_TO_ANALYSE)!;
      this.saveIncident();
    }
  }

  getProvisionForCustomerOrder(): Provision[] {
    let outProvisions = [];
    if (this.incident && this.asso) {
      for (let asso of this.asso) {
        for (let service of asso.services)
          for (let provision of service.provisions)
            outProvisions.push(provision)
      }
    }
    return outProvisions;
  }

}
