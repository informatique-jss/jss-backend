import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AffaireSearch } from '../../model/AffaireSearch';
import { AssoAffaireOrderFlatten } from '../../model/AssoAffaireOrderFlatten';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { AffaireComponent } from '../affaire/affaire.component';

@Component({
  selector: 'affaire-list',
  templateUrl: './affaire-list.component.html',
  styleUrls: ['./affaire-list.component.css']
})
export class AffaireListComponent implements OnInit {
  affaireSearch: AffaireSearch = {} as AffaireSearch;
  affaires: AssoAffaireOrderFlatten[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];

  currentEmployee: Employee | undefined;
  bookmark: any;

  constructor(
    private appService: AppService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private employeeService: EmployeeService,
    private formBuilder: FormBuilder,
    private userPreferenceService: UserPreferenceService,
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Affaires / Prestations");
    this.displayedColumns = [];

    this.bookmark = this.userPreferenceService.getUserSearchBookmark("affaires") as AffaireSearch;
    if (this.bookmark) {
      console.log(this.bookmark);
      this.affaireSearch.assignedTo = this.bookmark.assignedTo;
      this.affaireSearch.label = this.bookmark.label;
      this.affaireSearch.responsible = this.bookmark.responsible;
      this.affaireSearch.status = this.bookmark.status;
    }
    this.displayedColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire", valueFonction: ((element: any) => element.affaire.denomination ? element.affaire.denomination : element.affaire.firstname + " " + element.affaire.lastname) } as SortTableColumn);
    this.displayedColumns.push({ id: "affaireAddress", fieldName: "affaireAddress", label: "Adresse de l'affaire", valueFonction: ((element: any) => element.affaire.address + " - " + element.affaire.postalCode + " - " + element.affaire.city.label) } as SortTableColumn);
    this.displayedColumns.push({ id: "tiers", fieldName: "tiers", label: "Tiers", valueFonction: ((element: any) => element.customerOrder.tiers.denomination ? element.customerOrder.tiers.denomination : element.customerOrder.tiers.firstname + " " + element.customerOrder.tiers.lastname) } as SortTableColumn);
    this.displayedColumns.push({ id: "responsable", fieldName: "responsable", label: "Responsable", valueFonction: ((element: any) => (element.customerOrder.tiers.responsable) ? (element.customerOrder.tiers.responsable.denomination ? element.customerOrder.tiers.responsable.denomination : element.customerOrder.tiers.responsable.firstname + " " + element.customerOrder.tiers.responsable.lastname) : "") } as SortTableColumn);
    this.displayedColumns.push({ id: "confrere", fieldName: "confrere", label: "Confrère", valueFonction: ((element: any) => (element.customerOrder.tiers.confrere) ? (element.customerOrder.tiers.confrere.denomination ? element.customerOrder.tiers.confrere.denomination : element.customerOrder.tiers.confrere.firstname + " " + element.customerOrder.tiers.confrere.lastname) : "") } as SortTableColumn);
    this.displayedColumns.push({ id: "responsible", fieldName: "assignedTo", label: "Responsable de l'affaire", displayAsEmployee: true } as SortTableColumn);
    this.displayedColumns.push({ id: "assignedTo", fieldName: "provision.assignedTo", label: "Assignée à", displayAsEmployee: true } as SortTableColumn);
    this.displayedColumns.push({ id: "provisionType", fieldName: "provisionType", label: "Prestation", valueFonction: ((element: any) => element.provision.provisionFamilyType.label + " - " + element.provision.provisionType.label) } as SortTableColumn);
    this.displayedColumns.push({ id: "status", fieldName: "affaireStatus.label", label: "Statut", valueFonction: this.getStatusLabel } as SortTableColumn);
    this.getCurrentEmployee();

    this.tableAction.push({
      actionIcon: "visibility", actionName: "Voir l'affaire / prestation", actionLinkFunction: (action: SortTableAction, element: any) => {
        if (element)
          return ['/affaire', element.id, element.provision.id];
        return undefined;
      }, display: true,
    } as SortTableAction);
  }

  getCurrentEmployee() {
    this.employeeService.getCurrentEmployee().subscribe(response => this.currentEmployee = response);
  }

  getStatusLabel(element: any) {
    if (element)
      return AffaireComponent.getActiveWorkflowElementsForProvision(element.provision).label;
    return "";
  }

  affaireSearchForm = this.formBuilder.group({
  });

  searchAffaires() {
    if (this.affaireSearchForm.valid && (
      this.affaireSearch.assignedTo
      || this.affaireSearch.label
      || this.affaireSearch.responsible
      || this.affaireSearch.status
    )) {
      this.userPreferenceService.setUserSearchBookmark(this.affaireSearch, "affaires");
      this.assoAffaireOrderService.getAssoAffaireOrders(this.affaireSearch).subscribe(response => {
        this.affaires = [];
        if (response)
          for (let asso of response)
            if (asso.provisions)
              for (let provision of asso.provisions) {
                let flatAsso = {} as AssoAffaireOrderFlatten;
                flatAsso.affaire = asso.affaire;
                flatAsso.assignedTo = asso.assignedTo;
                flatAsso.customerOrder = asso.customerOrder;
                flatAsso.id = asso.id;
                flatAsso.provision = provision;
                this.affaires.push(flatAsso);
              }
      })
    } else {
      this.appService.displaySnackBar("Veuillez remplir au moins un filtre", true, 20);
    }
  }
}
