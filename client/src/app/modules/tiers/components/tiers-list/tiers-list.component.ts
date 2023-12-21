import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { ResponsableSearchResult } from '../../model/ResponsableSearchResult';
import { TiersSearch } from '../../model/TiersSearch';
import { TiersSearchResult } from '../../model/TiersSearchResult';
import { ResponsableSearchResultService } from '../../services/responsable.search.result.service';
import { TiersSearchResultService } from '../../services/tiers.search.result.service';

@Component({
  selector: 'tiers-list',
  templateUrl: './tiers-list.component.html',
  styleUrls: ['./tiers-list.component.css']
})
export class TiersListComponent implements OnInit {
  tiers: TiersSearchResult[] | undefined;
  responsables: ResponsableSearchResult[] | undefined;
  displayedColumnsResponsables: SortTableColumn[] = [];
  displayedColumnsTiers: SortTableColumn[] = [];
  tableActionResponsable: SortTableAction[] = [];
  tableActionTiers: SortTableAction[] = [];
  bookmark: TiersSearch | undefined;
  bookmarkResponsable: TiersSearch | undefined;
  tiersSearch: TiersSearch | undefined;
  responsableSearch: TiersSearch | undefined;
  allEmployees: Employee[] | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private userPreferenceService: UserPreferenceService,
    private tiersSearchResultService: TiersSearchResultService,
    private responsableSearchResultService: ResponsableSearchResultService,
    private employeeService: EmployeeService,
  ) { }

  ngOnInit() {
    this.employeeService.getEmployees().subscribe(response => {
      this.allEmployees = response;
      this.displayedColumnsResponsables = [];
      this.displayedColumnsTiers = [];
      this.tiersSearch = {} as TiersSearch;
      this.responsableSearch = {} as TiersSearch;
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

      this.displayedColumnsResponsables.push({ id: "tiersId", fieldName: "tiersId", label: "N°" } as SortTableColumn);
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

      this.displayedColumnsTiers.push({ id: "tiersId", fieldName: "tiersId", label: "N°" } as SortTableColumn);
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

    });
  }

  tiersSearchForm = this.formBuilder.group({
  });

  responsableSearchForm = this.formBuilder.group({
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
}
