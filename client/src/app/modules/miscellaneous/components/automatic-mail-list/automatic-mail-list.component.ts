import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { MatLegacyDialog as MatDialog } from '@angular/material/legacy-dialog';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { Quotation } from 'src/app/modules/quotation/model/Quotation';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
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

  displayedColumns: SortTableColumn[] = [];
  tableActions: SortTableAction[] = [] as Array<SortTableAction>;
  searchText: string | undefined;

  filterValue: string = "";

  attachmentTypeAutomaticMail: AttachmentType = this.constantService.getAttachmentTypeAutomaticMail();

  constructor(
    public confirmationDialog: MatDialog,
    protected uploadAttachmentService: UploadAttachmentService,
    private customerMailService: CustomerMailService,
    private constantService: ConstantService,
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
    this.displayedColumns.push({ id: "createdDateTime", fieldName: "createdDateTime", label: "Créé le", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "hasErrors", fieldName: "hasErrors", label: "En erreur ?", valueFonction: (element: any) => { return element.hasErrors ? "Oui" : "Non" } } as SortTableColumn);
    this.displayedColumns.push({ id: "isSent", fieldName: "isSent", label: "Envoyé ?", valueFonction: (element: any) => { return element.isSent ? "Oui" : "Non" } } as SortTableColumn);
    this.displayedColumns.push({ id: "sentTo", fieldName: "sentTo", label: "Envoyé à", valueFonction: this.getSentToLabel } as SortTableColumn);
    this.displayedColumns.push({ id: "sentCc", fieldName: "sentCc", label: "Copie à", valueFonction: this.getSentCcLabel } as SortTableColumn);
    this.displayedColumns.push({ id: "subject", fieldName: "subject", label: "Objet" } as SortTableColumn);

    this.tableActions.push({
      actionIcon: "visibility", actionName: "Prévisualiser le mail", actionClick: (action: SortTableAction, element: any): void => {
        let customerMail = element as CustomerMail;
        if (customerMail && customerMail.attachments)
          for (let attachment of customerMail.attachments)
            if (attachment.attachmentType && attachment.attachmentType.id == this.attachmentTypeAutomaticMail.id)
              this.uploadAttachmentService.previewAttachment(attachment);
      }, display: true
    } as SortTableAction);
    this.tableActions.push({
      actionIcon: "download", actionName: "Télécharger le mail", actionClick: (action: SortTableAction, element: any): void => {
        let customerMail = element as CustomerMail;
        if (customerMail && customerMail.attachments)
          for (let attachment of customerMail.attachments)
            if (attachment.attachmentType && attachment.attachmentType.id == this.attachmentTypeAutomaticMail.id)
              this.uploadAttachmentService.downloadAttachment(attachment);
      }, display: true
    } as SortTableAction);
  }

  getSentToLabel(element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string {
    if (element && element.sendToMe && element.sendToMeEmployee)
      return element.sendToMeEmployee.mail;
    if (element && column && element.mailComputeResult && element.mailComputeResult.recipientsMailTo)
      return element.mailComputeResult.recipientsMailTo.map((mail: { mail: any; }) => mail.mail).join(", ");
    return "";
  }

  getSentCcLabel(element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string {
    if (element && column && element.mailComputeResult && element.mailComputeResult.recipientsMailCc)
      return element.mailComputeResult.recipientsMailCc.map((mail: { mail: any; }) => mail.mail).join(", ");
    return "";
  }

  setDataTable() {
    this.customerMails.sort(function (a: CustomerMail, b: CustomerMail) {
      return new Date(b.createdDateTime).getTime() - new Date(a.createdDateTime).getTime();
    });
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }
}
