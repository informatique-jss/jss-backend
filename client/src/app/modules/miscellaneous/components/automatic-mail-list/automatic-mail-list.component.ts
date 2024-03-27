import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { Quotation } from 'src/app/modules/quotation/model/Quotation';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { AppService } from 'src/app/services/app.service';
import { Confrere } from '../../../quotation/model/Confrere';
import { CustomerOrder } from '../../../quotation/model/CustomerOrder';
import { Responsable } from '../../../tiers/model/Responsable';
import { AttachmentType } from '../../model/AttachmentType';
import { CustomerMail } from '../../model/CustomerMail';
import { SortTableAction } from '../../model/SortTableAction';
import { SortTableColumn } from '../../model/SortTableColumn';
import { ConstantService } from '../../services/constant.service';
import { CustomerMailService } from '../../services/customer.mail.service';
import { UploadAttachmentService } from '../../services/upload.attachment.service';

@Component({
  selector: 'automatic-mail-list',
  templateUrl: './automatic-mail-list.component.html',
  styleUrls: ['./automatic-mail-list.component.css']
})
export class AutomaticMailListComponent implements OnInit {
  @Input() tiers: Tiers | undefined;
  @Input() quotation: Quotation | undefined;
  @Input() customerOrder: CustomerOrder | undefined;
  @Input() confrere: Confrere | undefined;
  @Input() responsable: Responsable | undefined;

  customerMails: CustomerMail[] = [];

  displayedColumns: SortTableColumn<CustomerMail>[] = [];
  tableActions: SortTableAction<CustomerMail>[] = [] as Array<SortTableAction<CustomerMail>>;
  searchText: string | undefined;

  filterValue: string = "";
  refreshTable: Subject<void> = new Subject<void>();
  attachmentTypeAutomaticMail: AttachmentType = this.constantService.getAttachmentTypeAutomaticMail();

  constructor(
    public confirmationDialog: MatDialog,
    protected uploadAttachmentService: UploadAttachmentService,
    private customerMailService: CustomerMailService,
    private constantService: ConstantService,
    private appService: AppService,
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers || changes.quotation || changes.customerOrder) {
      this.setDataTable();
    }
  }

  ngOnInit() {
    if (this.quotation)
      this.customerMailService.getCustomerMailByQuotation(this.quotation).subscribe(response => this.customerMails = response);
    if (this.customerOrder)
      this.customerMailService.getCustomerMailByCustomerOrder(this.customerOrder).subscribe(response => this.customerMails = response);
    if (this.tiers)
      this.customerMailService.getCustomerMailByTiers(this.tiers).subscribe(response => this.customerMails = response);
    if (this.responsable)
      this.customerMailService.getCustomerMailByResponsable(this.responsable).subscribe(response => this.customerMails = response);
    if (this.confrere)
      this.customerMailService.getCustomerMailByConfrere(this.confrere).subscribe(response => this.customerMails = response);

    this.setDataTable();

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "createdDateTime", fieldName: "createdDateTime", label: "Créé le", valueFonction: formatDateTimeForSortTable } as SortTableColumn<CustomerMail>);
    this.displayedColumns.push({
      id: "toSendAfter", fieldName: "toSendAfter", label: "Envoi prévisionnel le", valueFonction: (element: CustomerMail, column: SortTableColumn<CustomerMail>) => {
        if (element.isSent == false && element.toSendAfter != null && element.isCancelled == false) {
          let toSendDate = new Date(element.toSendAfter);
          let nowDate = new Date();
          if (toSendDate.getTime() > nowDate.getTime())
            return formatDateTimeForSortTable(element, column);
        }
        return "";
      }
    } as SortTableColumn<CustomerMail>);
    this.displayedColumns.push({ id: "isSent", fieldName: "isSent", label: "Envoyé ?", valueFonction: (element: CustomerMail, column: SortTableColumn<CustomerMail>) => { return element.isSent ? "Oui" : "Non" } } as SortTableColumn<CustomerMail>);
    this.displayedColumns.push({ id: "isCancelled", fieldName: "isCancelled", label: "Annulé ?", valueFonction: (element: CustomerMail, column: SortTableColumn<CustomerMail>) => { return element.isCancelled ? "Oui" : "Non" } } as SortTableColumn<CustomerMail>);
    this.displayedColumns.push({ id: "sentTo", fieldName: "sentTo", label: "Envoyé à", valueFonction: this.getSentToLabel } as SortTableColumn<CustomerMail>);
    this.displayedColumns.push({ id: "sentCc", fieldName: "sentCc", label: "Copie à", valueFonction: this.getSentCcLabel } as SortTableColumn<CustomerMail>);
    this.displayedColumns.push({ id: "subject", fieldName: "subject", label: "Objet" } as SortTableColumn<CustomerMail>);

    this.tableActions.push({
      actionIcon: "visibility", actionName: "Prévisualiser le mail", actionClick: (column: SortTableAction<CustomerMail>, element: CustomerMail, event: any): void => {
        let customerMail = element as CustomerMail;
        if (customerMail && customerMail.attachments)
          for (let attachment of customerMail.attachments)
            if (attachment.attachmentType && attachment.attachmentType.id == this.attachmentTypeAutomaticMail.id)
              this.uploadAttachmentService.previewAttachment(attachment);
      }, display: true
    } as SortTableAction<CustomerMail>);
    this.tableActions.push({
      actionIcon: "download", actionName: "Télécharger le mail", actionClick: (column: SortTableAction<CustomerMail>, element: CustomerMail, event: any): void => {
        let customerMail = element as CustomerMail;
        if (customerMail && customerMail.attachments)
          for (let attachment of customerMail.attachments)
            if (attachment.attachmentType && attachment.attachmentType.id == this.attachmentTypeAutomaticMail.id)
              this.uploadAttachmentService.downloadAttachment(attachment);
      }, display: true
    } as SortTableAction<CustomerMail>);
    this.tableActions.push({
      actionIcon: "fast_forward", actionName: "Envoyer le mail immédiatement", actionClick: (column: SortTableAction<CustomerMail>, element: CustomerMail, event: any): void => {
        let customerMail = element as CustomerMail;
        if (customerMail && customerMail.isSent == false && customerMail.isCancelled == false && customerMail.toSendAfter != null) {
          this.customerMailService.sendCustomerMailImmediatly(element).subscribe(response => {
            this.appService.displaySnackBar("Le mail sera envoyé dans quelques secondes", false, 10);
          })
        }
      }, display: true
    } as SortTableAction<CustomerMail>);
  }

  getSentToLabel(element: CustomerMail, column: SortTableColumn<CustomerMail>): string {
    if (element && element.sendToMe && element.sendToMeEmployee)
      return element.sendToMeEmployee.mail;
    if (element && column && element.mailComputeResult && element.mailComputeResult.recipientsMailTo)
      return element.mailComputeResult.recipientsMailTo.map((mail: { mail: any; }) => mail.mail).join(", ");
    return "";
  }

  getSentCcLabel(element: CustomerMail, column: SortTableColumn<CustomerMail>): string {
    if (element && column && element.mailComputeResult && element.mailComputeResult.recipientsMailCc)
      return element.mailComputeResult.recipientsMailCc.map((mail: { mail: any; }) => mail.mail).join(", ");
    return "";
  }

  setDataTable() {
    this.customerMails.sort(function (a: CustomerMail, b: CustomerMail) {
      return new Date(b.createdDateTime).getTime() - new Date(a.createdDateTime).getTime();
    });
    this.refreshTable.next();
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }
}
