import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { SalesReclamationOrigin } from 'src/app/modules/miscellaneous/model/SalesReclamationOrigin';
import { SalesReclamationProblem } from 'src/app/modules/miscellaneous/model/SalesReclamationProblem';
import { SalesReclamationCause } from 'src/app/modules/miscellaneous/model/SalesReclamationCause';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { FormBuilder } from '@angular/forms';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AffaireService } from '../../../quotation/services/affaire.service';
import { SalesReclamationService } from '../../services/sales.reclamation.service';
import { SalesReclamation } from 'src/app/modules/miscellaneous/model/SalesReclamation';
import { Tiers } from '../../model/Tiers';
import { ActivatedRoute } from '@angular/router';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { Employee } from 'src/app/modules/profile/model/Employee';import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { IQuotation } from 'src/app/modules/quotation/model/IQuotation';
import { instanceOfCustomerOrder, instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { CustomerOrderService } from '../../../quotation/services/customer.order.service';


@Component({
  selector: 'complaint-tiers',
  templateUrl: './complaint-tiers.component.html',
  styleUrls: ['./complaint-tiers.component.css']
})

export class ComplaintTiersComponent implements OnInit, AfterContentChecked{

  @Input() quotation: IQuotation = {} as IQuotation;
  @Input() tiers: Tiers = {} as Tiers;
  @Input() editMode: boolean = false;

  selectedAffaire: Affaire | undefined;
  selectedReclamationProblem: SalesReclamationProblem | undefined;
  selectedReclamationOrigin: SalesReclamationOrigin | undefined;
  selectedReclamationCause:  SalesReclamationCause | undefined;
  reclamation: SalesReclamation = {} as SalesReclamation;
  reclamationList: SalesReclamation[] | undefined;
  selectedEmployee: Employee | undefined;
  observations: string | undefined;

  displayedColumnsReclamations:  SortTableColumn<SalesReclamation>[] = [];

  constructor(
    private affaireService: AffaireService,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private changeDetectorRef: ChangeDetectorRef,
    private salesReclamationService: SalesReclamationService,
    private customerOrderService: CustomerOrderService,) {
  }

  principalForm = this.formBuilder.group({
  });

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit(): void {

    this.displayedColumnsReclamations = [];

    this.displayedColumnsReclamations.push({ id: "id", fieldName: "id", label: "N° reclamation"  } as SortTableColumn<SalesReclamation>);
    this.displayedColumnsReclamations.push({ id: "complaintDate", fieldName: "complaintDate", label: "Date de reclamation", valueFonction: formatDateTimeForSortTable  } as SortTableColumn<SalesReclamation>);
    this.displayedColumnsReclamations.push({id: "responsableName", fieldName: "responsableName", label: "Nom responsable"  } as SortTableColumn<SalesReclamation>);
    this.displayedColumnsReclamations.push({ id: "affaire.denomination", fieldName: "affaire.denomination", label: "Affaire denomination"} as SortTableColumn<SalesReclamation>);
    this.displayedColumnsReclamations.push({ id: "salesProblem.label", fieldName: "salesProblem.label", label: "Problem" } as SortTableColumn<SalesReclamation>);
    this.displayedColumnsReclamations.push({ id: "salesCause.label", fieldName: "salesCause.label", label: "Cause" } as SortTableColumn<SalesReclamation>);
    this.displayedColumnsReclamations.push({ id: "salesOrigin.label", fieldName: "salesOrigin.label", label: "Origin" } as SortTableColumn<SalesReclamation>);
    this.displayedColumnsReclamations.push({ id: "observations", fieldName: "observations", label: "observations" } as SortTableColumn<SalesReclamation>);

    let idTiers: number = this.activatedRoute.snapshot.params.id;
    if (idTiers)
    this.loadReclamationsByTiersId(idTiers);
  }

  fillAffaire(entity: IndexEntity) {
    let obj = JSON.parse((entity.text as string));
    this.affaireService.getAffaire(entity.entityId).subscribe(affaire => {
      this.selectedAffaire = affaire;
    });
  }

  private loadReclamationsByTiersId(idTiers: number) {
    this.salesReclamationService.getReclamationsByTiersId(idTiers).subscribe(reclamations => {
      if (Array.isArray(reclamations) && reclamations.length > 0)
      this.reclamationList = reclamations;
    });
  }

  submitForm(){
    if (this.selectedAffaire && this.selectedReclamationCause && this.selectedReclamationOrigin && this.selectedReclamationProblem) {
    this.reclamation.affaire = this.selectedAffaire;
    this.reclamation.salesCause = this.selectedReclamationCause;
    this.reclamation.salesProblem = this.selectedReclamationProblem;
    this.reclamation.salesOrigin = this.selectedReclamationOrigin;
    this.reclamation.idTiers = this.tiers.id;
    this.reclamation.responsableName = this.selectedEmployee?.firstname + " " + this.selectedEmployee?.lastname;
    this.reclamation.observations = this.observations;

    let d = new Date();
    this.reclamation.complaintDate = new Date(d.getFullYear(), d.getMonth(), d.getDate());
    this.salesReclamationService.addOrUpdateReclamation(this.reclamation).subscribe(response => {
      this.reclamation = response;
    });
    }
  }

  updateAssignedToForCustomerOrder(employee: Employee) {
    if (this.editMode)
      return;
    if (instanceOfCustomerOrder(this.quotation))
      this.customerOrderService.updateAssignedToForCustomerOrder(this.quotation, employee).subscribe(response => {
      });
    if (instanceOfQuotation(this.quotation))
      this.customerOrderService.updateAssignedToForQuotation(this.quotation, employee).subscribe(response => {
      });
  }
}


