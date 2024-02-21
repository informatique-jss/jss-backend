import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { AmountDialogComponent } from 'src/app/modules/invoicing/components/amount-dialog/amount-dialog.component';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { Rff } from '../../model/Rff';
import { RffSearch } from '../../model/RffSearch';
import { RffService } from '../../services/rff.service';

@Component({
  selector: 'rff-list',
  templateUrl: './rff-list.component.html',
  styleUrls: ['./rff-list.component.css']
})
export class RffListComponent implements OnInit {
  rff: Rff[] | undefined;
  displayedColumnsRff: SortTableColumn<Rff>[] = [];
  tableActionRff: SortTableAction<Rff>[] = [];
  bookmarkRff: RffSearch | undefined;
  @Input() rffSearch: RffSearch | undefined;
  @Input() isForTiersIntegration: boolean = false;
  allEmployees: Employee[] | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private userPreferenceService: UserPreferenceService,
    private employeeService: EmployeeService,
    private rffService: RffService,
    private constantService: ConstantService,
    private appService: AppService,
    public confirmationDialog: MatDialog,
    private habilitationsService: HabilitationsService
  ) { }

  ngOnInit() {
    this.employeeService.getEmployees().subscribe(response => {
      this.allEmployees = response;
      this.displayedColumnsRff = [];

      this.displayedColumnsRff.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "tiersId", fieldName: "tiersId", label: "N° Tiers" } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "tiersLabel", fieldName: "tiersLabel", label: "Tiers" } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "responsableLabel", fieldName: "responsableLabel", label: "Responsable" } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "startDate", fieldName: "startDate", label: "Début", valueFonction: formatDateForSortTable } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "endDate", fieldName: "endDate", label: "Fin", valueFonction: formatDateForSortTable } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL", valueFonction: formatEurosForSortTable } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités", valueFonction: formatEurosForSortTable } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "rffTotal", fieldName: "rffTotal", label: "Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "rib", fieldName: "rib", label: "RIB", valueFonction: (element: Rff, column: SortTableColumn<Rff>) => { return (element.rffIban) ? (element.rffIban + "/" + element.rffBic) : "" } } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "rffMail", fieldName: "rffMail", label: "Mail" } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "isCancelled", fieldName: "isCancelled", label: "Annulé ?", valueFonction: (element: Rff, column: SortTableColumn<Rff>) => { return element.isCancelled ? 'Oui' : 'Non' } } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({ id: "isSent", fieldName: "isSent", label: "Envoyé ?", valueFonction: (element: Rff, column: SortTableColumn<Rff>) => { return element.isSent ? 'Oui' : 'Non' } } as SortTableColumn<Rff>);
      this.displayedColumnsRff.push({
        id: "invoice", fieldName: "invoice", label: "Facture", displayAsStatus: true, statusFonction: (element: Rff) => {
          if (element.invoices && element.invoices.length > 0) {
            for (let invoice of element.invoices) {
              if (invoice.invoiceStatus.id != this.constantService.getInvoiceStatusCancelled().id)
                return invoice.invoiceStatus.code;
              return element.invoices[0].invoiceStatus.code;
            }
          }
          return "N/A";
        }, valueFonction: (element: Rff, column: SortTableColumn<Rff>) => {
          if (element.invoices && element.invoices.length > 0) {
            for (let invoice of element.invoices) {
              if (invoice.invoiceStatus.id != this.constantService.getInvoiceStatusCancelled().id)
                return invoice.invoiceStatus.label;
              return element.invoices[0].invoiceStatus.label;
            }
          }
          return "";
        }
      } as SortTableColumn<Rff>);

      this.tableActionRff.push({
        actionIcon: "visibility", actionName: "Voir le tiers", actionLinkFunction: (action: SortTableAction<Rff>, element: any) => {
          if (element)
            return ['/tiers', element.tiersId];
          return undefined;
        }, display: true,
      } as SortTableAction<Rff>);
      this.tableActionRff.push({
        actionIcon: "visibility", actionName: "Voir le responsable", actionLinkFunction: (action: SortTableAction<Rff>, element: any) => {
          if (element)
            return ['/tiers/responsable', element.responsableId];
          return undefined;
        }, display: true,
      } as SortTableAction<Rff>);
      this.tableActionRff.push({
        actionIcon: "do_not_disturb_on", actionName: "Ne pas verser le RFF", actionClick: (column: SortTableAction<Rff>, record: Rff, event: any) => {
          if (record.isCancelled == false) {
            const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
              maxWidth: "400px",
              data: {
                title: "Ne pas verser le RFF",
                content: "Êtes-vous sûr de ne pas vouloir verser le RFF à ces tiers/responsables ? Cette action est irréversible !",
                closeActionText: "Annuler",
                validationActionText: "Ne pas verser"
              }
            });

            dialogRef.afterClosed().subscribe(dialogResult => {
              if (dialogResult)
                this.rffService.cancelRff(record).subscribe(res => {
                  this.searchRff();
                })
            });
          }
        }, display: true,
      } as SortTableAction<Rff>);

      this.tableActionRff.push({
        actionIcon: "forward_to_inbox", actionName: "Envoyer le mail de RFF", actionClick: (column: SortTableAction<Rff>, element: Rff, event: any) => {
          if (element.isCancelled == false && element.isSent == false) {
            this.sendRffMail(element, false);
          }
        }, display: true,
      } as SortTableAction<Rff>);

      this.tableActionRff.push({
        actionIcon: "forward_to_inbox", actionName: "M'envoyer le mail de RFF", actionClick: (column: SortTableAction<Rff>, element: Rff, event: any) => {
          if (element.isCancelled == false && element.isSent == false) {
            this.sendRffMail(element, true);
          }
        }, display: true,
      } as SortTableAction<Rff>);

      if (this.habilitationsService.canAddNewInvoice())
        this.tableActionRff.push({
          actionIcon: "point_of_sale", actionName: "Générer la facture du RFF", actionClick: (column: SortTableAction<Rff>, element: Rff, event: any) => {
            if (element.isCancelled == false && element.isSent == true && this.habilitationsService.canAddNewInvoice()) {
              this.createInvoiceFromRff(element, event);
            }
          }, display: true,
        } as SortTableAction<Rff>);

      this.tableActionRff.push({
        actionIcon: "receipt_long", actionName: "Voir le détail de la facture", actionLinkFunction: (action: SortTableAction<Rff>, element: Rff) => {

          if (element.invoices && element.invoices.length > 0) {
            for (let invoice of element.invoices) {
              if (invoice.invoiceStatus.id != this.constantService.getInvoiceStatusCancelled().id)
                return ['/invoicing/view', invoice.id];
            }
          }

          return undefined;
        }, display: true,
      } as SortTableAction<Rff>);


      if (this.isForTiersIntegration && this.rffSearch)
        this.searchRff();
      else {
        this.bookmarkRff = this.userPreferenceService.getUserSearchBookmark("rff") as RffSearch;
        if (this.bookmarkRff) {
          this.rffSearch = this.bookmarkRff;
          if (this.rffSearch.startDate)
            this.rffSearch.startDate = new Date(this.rffSearch.startDate);
          if (this.rffSearch.endDate)
            this.rffSearch.endDate = new Date(this.rffSearch.endDate);
          this.searchRff();
        } else
          this.rffSearch = {} as RffSearch;
      }
    });
  }

  rffSearchForm = this.formBuilder.group({
  });

  searchRff() {
    if (this.rffSearchForm.valid && this.rffSearch) {
      if (!this.rffSearch.startDate || !this.rffSearch.endDate) {
        this.appService.displaySnackBar("Choisir une période de génération des RFF", true, 10);
        return;
      }
      if (!this.isForTiersIntegration)
        this.userPreferenceService.setUserSearchBookmark(this.rffSearch, "rff");
      this.rffService.getRffs(this.rffSearch).subscribe(response => {
        this.rff = response;
      })
    }
  }

  sendRffMail(rff: Rff, sendToMe: boolean) {
    if (sendToMe) {
      this.rffService.sendRff(rff, rff.rffTotal, sendToMe).subscribe();
      return;
    }
    if (rff.rffIban && rff.rffBic && rff.rffBic.length > 0 && rff.rffIban.length > 0) {
      const ribDialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
        maxWidth: "400px",
        data: {
          title: "Confirmation du RIB",
          content: "Le RFF sera versé sur le RIB : " + rff.rffIban + "/" + rff.rffBic + ". Il est modifiable dans la partie RFF du tiers/responsable.",
          closeActionText: "Annuler",
          validationActionText: "Confirmer le RIB"
        }
      });

      ribDialogRef.afterClosed().subscribe(ribDialogResult => {
        if (ribDialogResult) {
          const mailDialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
            maxWidth: "400px",
            data: {
              title: "Mail d'envoi du RIB",
              content: "Le mail de RFF sera envoyé à " + rff.rffMail + ". Il est modifiable dans la partie RFF du tiers/responsable.",
              closeActionText: "Annuler",
              validationActionText: "Confirmer le mail"
            }
          });
          mailDialogRef.afterClosed().subscribe(mailDialogResult => {

            if (mailDialogResult) {
              let amountDialogRef = this.confirmationDialog.open(AmountDialogComponent, {
                width: '60%'
              });

              amountDialogRef.componentInstance.title = "Montant HT du RFF à verser";
              amountDialogRef.componentInstance.label = "Indiquer le montant à verser au titre des RFF :";
              //amountDialogRef.componentInstance.maxAmount = Math.round((rff.rffTotal) * 100) / 100;
              amountDialogRef.afterClosed().subscribe(response => {
                if (response != null) {
                  this.rffService.sendRff(rff, response, sendToMe).subscribe(res => this.searchRff());
                } else {
                  return;
                }
              });
            }
          });
        }
      });
    } else {
      this.appService.displaySnackBar("Aucun RIB trouvé. Merci de compléter la partie RFF du tiers/responsable avant de poursuivre", true, 10);
    }
  }

  createInvoiceFromRff(rff: Rff, event: any) {
    if (this.rff)
      this.appService.openRoute(event, 'invoicing/rff/add/' + rff.id, null);
  }
}
