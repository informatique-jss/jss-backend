import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { ResponsableSearchResult } from '../../model/ResponsableSearchResult';
import { Rff } from '../../model/Rff';
import { RffSearch } from '../../model/RffSearch';
import { TiersSearch } from '../../model/TiersSearch';
import { TiersSearchResult } from '../../model/TiersSearchResult';
import { ResponsableSearchResultService } from '../../services/responsable.search.result.service';
import { RffService } from '../../services/rff.service';
import { TiersSearchResultService } from '../../services/tiers.search.result.service';

@Component({
  selector: 'tiers-list',
  templateUrl: './tiers-list.component.html',
  styleUrls: ['./tiers-list.component.css']
})
export class TiersListComponent implements OnInit {
  tiers: TiersSearchResult[] | undefined;
  responsables: ResponsableSearchResult[] | undefined;
  rff: Rff[] | undefined;
  displayedColumnsResponsables: SortTableColumn[] = [];
  displayedColumnsTiers: SortTableColumn[] = [];
  displayedColumnsRff: SortTableColumn[] = [];
  tableActionResponsable: SortTableAction[] = [];
  tableActionRff: SortTableAction[] = [];
  tableActionTiers: SortTableAction[] = [];
  bookmark: TiersSearch | undefined;
  bookmarkResponsable: TiersSearch | undefined;
  bookmarkRff: RffSearch | undefined;
  tiersSearch: TiersSearch | undefined;
  rffSearch: RffSearch | undefined;
  responsableSearch: TiersSearch | undefined;
  allEmployees: Employee[] | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private userPreferenceService: UserPreferenceService,
    private tiersSearchResultService: TiersSearchResultService,
    private responsableSearchResultService: ResponsableSearchResultService,
    private employeeService: EmployeeService,
    private rffService: RffService,
    private constantService: ConstantService,
    private appService: AppService,
    public confirmationDialog: MatDialog,
  ) { }

  ngOnInit() {
    this.employeeService.getEmployees().subscribe(response => {
      this.allEmployees = response;
      this.displayedColumnsResponsables = [];
      this.displayedColumnsRff = [];
      this.displayedColumnsTiers = [];
      this.tiersSearch = {} as TiersSearch;
      this.responsableSearch = {} as TiersSearch;
      this.rffSearch = {} as RffSearch;
      this.bookmark = this.userPreferenceService.getUserSearchBookmark("tiers") as TiersSearch;
      if (this.bookmark) {
        this.tiersSearch.tiers = this.bookmark.tiers;
        this.tiersSearch.responsable = this.bookmark.responsable;
        this.tiersSearch.salesEmployee = this.bookmark.salesEmployee;
        this.tiersSearch.startDate = this.bookmark.startDate;
        this.tiersSearch.endDate = this.bookmark.endDate;
      }

      this.bookmarkResponsable = this.userPreferenceService.getUserSearchBookmark("responsables") as TiersSearch;
      if (this.bookmark) {
        this.responsableSearch.tiers = this.bookmark.tiers;
        this.responsableSearch.responsable = this.bookmark.responsable;
        this.responsableSearch.salesEmployee = this.bookmark.salesEmployee;
        this.responsableSearch.startDate = this.bookmark.startDate;
        this.responsableSearch.endDate = this.bookmark.endDate;
      }

      this.bookmarkRff = this.userPreferenceService.getUserSearchBookmark("rff") as RffSearch;
      if (this.bookmark) {
        this.rffSearch.tiers = this.bookmarkRff.tiers;
        this.rffSearch.responsable = this.bookmarkRff.responsable;
        this.rffSearch.salesEmployee = this.bookmarkRff.salesEmployee;
        this.rffSearch.startDate = this.bookmarkRff.startDate;
        this.rffSearch.endDate = this.bookmarkRff.endDate;
      }

      this.displayedColumnsResponsables.push({ id: "tiersLabel", fieldName: "tiersLabel", label: "Tiers" } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "tiersCategory", fieldName: "tiersCategory", label: "Catégorie du tiers" } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "responsableLabel", fieldName: "responsableLabel", label: "Responsable" } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "responsableCategory", fieldName: "responsableCategory", label: "Catégorie du responsable" } as SortTableColumn);
      this.displayedColumnsResponsables.push({
        id: "salesEmployee", fieldName: "salesEmployeeId", label: "Commercial", displayAsEmployee: true, valueFonction: (element: any) => {
          if (element && this.allEmployees) {
            for (let employee of this.allEmployees)
              if (employee.id == element.salesEmployeeId)
                return employee;
          }
          return undefined;
        }
      } as SortTableColumn);

      this.displayedColumnsResponsables.push({ id: "firstOrderDay", fieldName: "firstOrderDay", label: "1ère commande", valueFonction: formatDateForSortTable } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "lastOrderDay", fieldName: "lastOrderDay", label: "Dernière commande", valueFonction: formatDateForSortTable } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "createdDateDay", fieldName: "createdDateDay", label: "Création", valueFonction: formatDateForSortTable } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "lastResponsableFollowupDate", fieldName: "lastResponsableFollowupDate", label: "Dernier suivi", valueFonction: formatDateForSortTable } as SortTableColumn);

      this.displayedColumnsResponsables.push({ id: "announcementJssNbr", fieldName: "announcementJssNbr", label: "Nbr annonces JSS" } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "announcementConfrereNbr", fieldName: "announcementConfrereNbr", label: "Nbr annonces confrère" } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "announcementNbr", fieldName: "announcementNbr", label: "Nbr annonces" } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "formalityNbr", fieldName: "formalityNbr", label: "Nbr formalités" } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "billingLabelType", fieldName: "billingLabelType", label: "Type de facturation" } as SortTableColumn);

      this.displayedColumnsResponsables.push({ id: "turnoverAmountWithoutTax", fieldName: "turnoverAmountWithoutTax", label: "CA HT", valueFonction: formatEurosForSortTable } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "turnoverAmountWithTax", fieldName: "turnoverAmountWithTax", label: "CA TTC", valueFonction: formatEurosForSortTable } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "turnoverAmountWithoutDebourWithoutTax", fieldName: "turnoverAmountWithoutDebourWithoutTax", label: "CA HT hors débours", valueFonction: formatEurosForSortTable } as SortTableColumn);
      this.displayedColumnsResponsables.push({ id: "turnoverAmountWithoutDebourWithTax", fieldName: "turnoverAmountWithoutDebourWithTax", label: "CA TTC hors débours", valueFonction: formatEurosForSortTable } as SortTableColumn);

      this.tableActionResponsable.push({
        actionIcon: "visibility", actionName: "Voir le tiers", actionLinkFunction: (action: SortTableAction, element: any) => {
          if (element)
            return ['/tiers', element.tiersId];
          return undefined;
        }, display: true,
      } as SortTableAction);
      this.tableActionResponsable.push({
        actionIcon: "visibility", actionName: "Voir le responsable", actionLinkFunction: (action: SortTableAction, element: any) => {
          if (element)
            return ['/tiers/responsable', element.responsableId];
          return undefined;
        }, display: true,
      } as SortTableAction);

      this.displayedColumnsTiers.push({ id: "tiersLabel", fieldName: "tiersLabel", label: "Tiers" } as SortTableColumn);
      this.displayedColumnsTiers.push({ id: "tiersCategory", fieldName: "tiersCategory", label: "Catégorie du tiers" } as SortTableColumn);
      this.displayedColumnsTiers.push({
        id: "salesEmployee", fieldName: "salesEmployeeId", label: "Commercial", displayAsEmployee: true, valueFonction: (element: any) => {
          if (element && this.allEmployees) {
            for (let employee of this.allEmployees)
              if (employee.id == element.salesEmployeeId)
                return employee;
          }
          return undefined;
        }
      } as SortTableColumn);

      this.displayedColumnsTiers.push({ id: "firstOrderDay", fieldName: "firstOrderDay", label: "1ère commande", valueFonction: formatDateForSortTable } as SortTableColumn);
      this.displayedColumnsTiers.push({ id: "lastOrderDay", fieldName: "lastOrderDay", label: "Dernière commande", valueFonction: formatDateForSortTable } as SortTableColumn);
      this.displayedColumnsTiers.push({ id: "createdDateDay", fieldName: "createdDateDay", label: "Création", valueFonction: formatDateForSortTable } as SortTableColumn);
      this.displayedColumnsTiers.push({ id: "lastResponsableFollowupDate", fieldName: "lastResponsableFollowupDate", label: "Dernier suivi", valueFonction: formatDateForSortTable } as SortTableColumn);

      this.displayedColumnsTiers.push({ id: "announcementJssNbr", fieldName: "announcementJssNbr", label: "Nbr annonces JSS" } as SortTableColumn);
      this.displayedColumnsTiers.push({ id: "announcementConfrereNbr", fieldName: "announcementConfrereNbr", label: "Nbr annonces confrère" } as SortTableColumn);
      this.displayedColumnsTiers.push({ id: "announcementNbr", fieldName: "announcementNbr", label: "Nbr annonces" } as SortTableColumn);
      this.displayedColumnsTiers.push({ id: "formalityNbr", fieldName: "formalityNbr", label: "Nbr formalités" } as SortTableColumn);
      this.displayedColumnsTiers.push({ id: "billingLabelType", fieldName: "billingLabelType", label: "Type de facturation" } as SortTableColumn);

      this.displayedColumnsTiers.push({ id: "turnoverAmountWithoutTax", fieldName: "turnoverAmountWithoutTax", label: "CA HT", valueFonction: formatEurosForSortTable, sortFonction: (element: any) => { return (element.turnoverAmountWithoutTax) } } as SortTableColumn);
      this.displayedColumnsTiers.push({ id: "turnoverAmountWithTax", fieldName: "turnoverAmountWithTax", label: "CA TTC", valueFonction: formatEurosForSortTable, sortFonction: (element: any) => { return (element.turnoverAmountWithTax) } } as SortTableColumn);
      this.displayedColumnsTiers.push({ id: "turnoverAmountWithoutDebourWithoutTax", fieldName: "turnoverAmountWithoutDebourWithoutTax", label: "CA HT hors débours", valueFonction: formatEurosForSortTable, sortFonction: (element: any) => { return (element.turnoverAmountWithoutDebourWithoutTax) } } as SortTableColumn);
      this.displayedColumnsTiers.push({ id: "turnoverAmountWithoutDebourWithTax", fieldName: "turnoverAmountWithoutDebourWithTax", label: "CA TTC hors débours", valueFonction: formatEurosForSortTable, sortFonction: (element: any) => { return (element.turnoverAmountWithoutDebourWithTax) } } as SortTableColumn);

      this.tableActionTiers.push({
        actionIcon: "visibility", actionName: "Voir le tiers", actionLinkFunction: (action: SortTableAction, element: any) => {
          if (element)
            return ['/tiers', element.tiersId];
          return undefined;
        }, display: true,
      } as SortTableAction);


      this.rffSearch.isHideCancelledRff = true;
      this.displayedColumnsRff.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn);
      this.displayedColumnsRff.push({ id: "tiersLabel", fieldName: "tiersLabel", label: "Tiers" } as SortTableColumn);
      this.displayedColumnsRff.push({ id: "responsableLabel", fieldName: "responsableLabel", label: "Responsable" } as SortTableColumn);
      this.displayedColumnsRff.push({ id: "startDate", fieldName: "startDate", label: "Début", valueFonction: formatDateForSortTable } as SortTableColumn);
      this.displayedColumnsRff.push({ id: "endDate", fieldName: "endDate", label: "Fin", valueFonction: formatDateForSortTable } as SortTableColumn);
      this.displayedColumnsRff.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL", valueFonction: formatEurosForSortTable, sortFonction: (element: any) => { return (element.turnoverAmountWithoutDebourWithTax) } } as SortTableColumn);
      this.displayedColumnsRff.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités", valueFonction: formatEurosForSortTable, sortFonction: (element: any) => { return (element.turnoverAmountWithoutDebourWithTax) } } as SortTableColumn);
      this.displayedColumnsRff.push({ id: "isCancelled", fieldName: "isCancelled", label: "Annulé ?", valueFonction: (element: Rff) => { return element.isCancelled ? 'Oui' : 'Non' } } as SortTableColumn);
      this.displayedColumnsRff.push({ id: "isSent", fieldName: "isSent", label: "Envoyé ?", valueFonction: (element: Rff) => { return element.isSent ? 'Oui' : 'Non' } } as SortTableColumn);
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
        }, valueFonction: (element: Rff) => {
          if (element.invoices && element.invoices.length > 0) {
            for (let invoice of element.invoices) {
              if (invoice.invoiceStatus.id != this.constantService.getInvoiceStatusCancelled().id)
                return invoice.invoiceStatus.label;
              return element.invoices[0].invoiceStatus.label;
            }
          }
          return "N/A";
        }
      } as SortTableColumn);

      this.tableActionRff.push({
        actionIcon: "visibility", actionName: "Voir le tiers", actionLinkFunction: (action: SortTableAction, element: any) => {
          if (element)
            return ['/tiers', element.tiersId];
          return undefined;
        }, display: true,
      } as SortTableAction);
      this.tableActionRff.push({
        actionIcon: "visibility", actionName: "Voir le responsable", actionLinkFunction: (action: SortTableAction, element: any) => {
          if (element)
            return ['/tiers/responsable', element.responsableId];
          return undefined;
        }, display: true,
      } as SortTableAction);
      this.tableActionRff.push({
        actionIcon: "do_not_disturb_on", actionName: "Ne pas verser le RFF", actionClick: (action: SortTableAction, element: Rff) => {

          if (element.isCancelled == false) {
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
                this.rffService.cancelRff(element).subscribe(res => {
                  this.searchRff();
                })
            });
          }


        }, display: true,
      } as SortTableAction);
    });
  }

  tiersSearchForm = this.formBuilder.group({
  });

  responsableSearchForm = this.formBuilder.group({
  });

  rffSearchForm = this.formBuilder.group({
  });

  searchResponsables() {
    if (this.tiersSearchForm.valid && this.responsableSearch) {
      this.userPreferenceService.setUserSearchBookmark(this.responsableSearch, "responsables");
      this.responsableSearchResultService.getResponsableSearch(this.responsableSearch).subscribe(response => {
        this.responsables = response;
      })
    }
  }

  searchTiers() {
    if (this.tiersSearchForm.valid && this.tiersSearch) {
      this.userPreferenceService.setUserSearchBookmark(this.tiersSearch, "tiers");
      this.tiersSearchResultService.getTiersSearch(this.tiersSearch).subscribe(response => {
        this.tiers = response;
      })
    }
  }

  searchRff() {
    if (this.rffSearchForm.valid && this.rffSearch) {
      if (!this.rffSearch.startDate || !this.rffSearch.endDate) {
        this.appService.displaySnackBar("Choisir une période de génération des RFF", true, 10);
        return;
      }
      this.userPreferenceService.setUserSearchBookmark(this.tiersSearch, "rff");
      this.rffService.getRffs(this.rffSearch).subscribe(response => {
        this.rff = response;
      })
    }
  }
}
