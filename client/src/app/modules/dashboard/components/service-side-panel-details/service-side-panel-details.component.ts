import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionPanel } from '@angular/material/expansion';
import { SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from 'src/app/libs/Constants';
import { getAffaireFromAssoAffaireOrder } from 'src/app/modules/invoicing/components/invoice-tools';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { IAttachment } from 'src/app/modules/miscellaneous/model/IAttachment';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { SelectMultiServiceTypeDialogComponent } from 'src/app/modules/quotation/components/select-multi-service-type-dialog/select-multi-service-type-dialog.component';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { AssoAffaireOrder } from 'src/app/modules/quotation/model/AssoAffaireOrder';
import { Provision } from 'src/app/modules/quotation/model/Provision';
import { Service } from 'src/app/modules/quotation/model/Service';
import { AffaireService } from 'src/app/modules/quotation/services/affaire.service';
import { AssoAffaireOrderService } from 'src/app/modules/quotation/services/asso.affaire.order.service';
import { ServiceService } from 'src/app/modules/quotation/services/service.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { Notification } from '../../../../modules/miscellaneous/model/Notification';

@Component({
  selector: 'service-side-panel-details',
  templateUrl: './service-side-panel-details.component.html',
  styleUrls: ['./service-side-panel-details.component.css'],
  viewProviders: [MatExpansionPanel]
})
export class ServiceSidePanelDetailsComponent implements OnInit {

  @Input() service: Service | undefined;
  @Input() provision: Provision | undefined;
  @Input() isExpanded: boolean = false;

  serviceAttachments: IAttachment = { id: 1, attachments: [] as Attachment[] } as IAttachment;

  SERVICE_FIELD_TYPE_TEXT = SERVICE_FIELD_TYPE_TEXT;
  SERVICE_FIELD_TYPE_INTEGER = SERVICE_FIELD_TYPE_INTEGER;
  SERVICE_FIELD_TYPE_DATE = SERVICE_FIELD_TYPE_DATE;
  SERVICE_FIELD_TYPE_TEXTAREA = SERVICE_FIELD_TYPE_TEXTAREA;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;

  @Output() triggerRefreshEntity = new EventEmitter<void>();

  serviceNotification: Notification[][] = [];
  affaireNotification: Notification[][] = [];

  constructor(private formBuilder: FormBuilder,
    private habilitationsService: HabilitationsService,
    private serviceService: ServiceService,
    public confirmationDialog: MatDialog,
    public selectServiceTypeDialog: MatDialog,
    private habilitationService: HabilitationsService,
    private notificationService: NotificationService,
    private appService: AppService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private affaireService: AffaireService
  ) { }

  serviceForm = this.formBuilder.group({});
  getAffaireFromAssoAffaireOrder = getAffaireFromAssoAffaireOrder;

  ngOnInit() {
    this.serviceAttachments = { id: 1, attachments: [] as Attachment[] } as IAttachment;
    if (this.service)
      if (this.service.assoServiceDocuments)
        for (let doc of this.service.assoServiceDocuments)
          this.serviceAttachments.attachments.push(...doc.attachments);

    if (this.provision && this.service)
      this.affaireService.getAffaire(this.service.assoAffaireOrder.affaire.id).subscribe(response => {
        this.service!.assoAffaireOrder.affaire = response;
      })
  }

  addNewNotificationOnAffaire(affaire: Affaire) {
    this.appService.addPersonnalNotification(() => this.affaireNotification = [], this.affaireNotification ? this.affaireNotification[affaire.id] : undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, affaire);
  }

  getNotificationForAffaire(affaire: Affaire) {
    if (this.affaireNotification[affaire.id] == undefined) {
      this.affaireNotification[affaire.id] = [];
      this.notificationService.getNotificationsForAffaire(affaire.id).subscribe(response => this.affaireNotification[affaire.id] = response);
    }
    return this.affaireNotification[affaire.id];
  }

  updateAssignedToForAffaire(employee: any, asso: AssoAffaireOrder) {
    this.assoAffaireOrderService.updateAssignedToForAsso(asso, employee).subscribe(response => {
    });
  }

  displayAffaire(event: any, asso: AssoAffaireOrder) {
    this.appService.openRoute({ ctrlKey: true }, '/affaire/' + asso.affaire.id, null);
  }

  addNewNotificationOnService(service: Service) {
    this.appService.addPersonnalNotification(() => this.serviceNotification = [], this.serviceNotification ? this.serviceNotification[service.id] : undefined, undefined, undefined, service, undefined, undefined, undefined, undefined, undefined);
  }

  getNotificationForService(service: Service) {
    if (this.serviceNotification[service.id] == undefined) {
      this.serviceNotification[service.id] = [];
      this.notificationService.getNotificationsForService(service.id).subscribe(response => this.serviceNotification[service.id] = response);
    }
    return this.serviceNotification[service.id];
  }

  canDisplayNotifications() {
    return this.habilitationService.canDisplayNotifications();
  }

  deleteService(service: Service) {
    if (!this.habilitationsService.canByPassProvisionLockOnBilledOrder())
      return;
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Supprimer le service",
        content: "Êtes-vous sûr de vouloir continuer ?",
        closeActionText: "Annuler",
        validationActionText: "Confirmer"
      }
    });

    dialogRef.afterClosed().subscribe(response => {
      if (response) {
        this.serviceService.deleteService(service).subscribe(response => {
          this.triggerRefreshEntity.next();
        });
      }
    });
  }

  modifyService(service: Service) {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Modifier le type de service",
        content: "Attention, la modification du type de service ajoutera les nouvelles prestations sans supprimer les prestations existantes. Pensez à vérifier les doublons après modification. Êtes-vous sûr de vouloir continuer ?",
        closeActionText: "Annuler",
        validationActionText: "Modifier"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult && service) {
        const dialogRef2 = this.selectServiceTypeDialog.open(SelectMultiServiceTypeDialogComponent, {
          width: "50%",
        });
        dialogRef2.componentInstance.isJustSelectServiceType = true;
        dialogRef2.afterClosed().subscribe(dialogResult => {
          if (dialogResult && service) {
            this.serviceService.modifyServiceType(service, dialogResult).subscribe(response => {
              this.triggerRefreshEntity.next();
            })
          }
        });
      }
    });
  }



}
