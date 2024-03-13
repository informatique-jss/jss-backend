import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { SalesComplainOrigin } from 'src/app/modules/miscellaneous/model/SalesComplainOrigin';
import { SalesComplainProblem } from 'src/app/modules/miscellaneous/model/SalesComplainProblem';
import { SalesComplainCause } from 'src/app/modules/miscellaneous/model/SalesComplainCause';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { FormBuilder } from '@angular/forms';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AffaireService } from '../../../quotation/services/affaire.service';
import { SalesComplainService } from '../../services/sales.complain.service';
import { SalesComplain } from 'src/app/modules/miscellaneous/model/SalesComplain';
import { Tiers } from '../../model/Tiers';
import { ActivatedRoute } from '@angular/router';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { Employee } from 'src/app/modules/profile/model/Employee';
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
  selectedComplainProblem: SalesComplainProblem | undefined;
  selectedComplainOrigin: SalesComplainOrigin | undefined;
  selectedComplainCause:  SalesComplainCause | undefined;
  complain: SalesComplain = {} as SalesComplain;
  complainList: SalesComplain[] | undefined;
  selectedEmployee: Employee | undefined;
  observations: string | undefined;
  customerOrderNumber: string | undefined;

  displayedColumnsComplains:  SortTableColumn<SalesComplain>[] = [];

  constructor(
    private affaireService: AffaireService,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private changeDetectorRef: ChangeDetectorRef,
    private salesComplainService: SalesComplainService,
    private customerOrderService: CustomerOrderService,) {
  }

  principalForm = this.formBuilder.group({
  });

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit(): void {

    this.displayedColumnsComplains = [];

    this.displayedColumnsComplains.push({ id: "id", fieldName: "id", label: "N° reclamation"  } as SortTableColumn<SalesComplain>);
    this.displayedColumnsComplains.push({ id: "customerOrderNumber", fieldName: "customerOrderNumber", label: "Numero de la commande associée" } as SortTableColumn<SalesComplain>);
    this.displayedColumnsComplains.push({ id: "complaintDate", fieldName: "complaintDate", label: "Date de reclamation", valueFonction: formatDateTimeForSortTable  } as SortTableColumn<SalesComplain>);
    this.displayedColumnsComplains.push({id: "responsableName", fieldName: "responsableName", label: "Nom responsable"  } as SortTableColumn<SalesComplain>);
    this.displayedColumnsComplains.push({ id: "affaire.denomination", fieldName: "affaire.denomination", label: "Affaire denomination"} as SortTableColumn<SalesComplain>);
    this.displayedColumnsComplains.push({ id: "salesProblem.label", fieldName: "salesProblem.label", label: "Problem" } as SortTableColumn<SalesComplain>);
    this.displayedColumnsComplains.push({ id: "salesCause.label", fieldName: "salesCause.label", label: "Cause" } as SortTableColumn<SalesComplain>);
    this.displayedColumnsComplains.push({ id: "salesOrigin.label", fieldName: "salesOrigin.label", label: "Origin" } as SortTableColumn<SalesComplain>);
    this.displayedColumnsComplains.push({ id: "observations", fieldName: "observations", label: "observations" } as SortTableColumn<SalesComplain>);

    let idTiers: number = this.activatedRoute.snapshot.params.id;
    if (idTiers)
    this.loadComplainsByTiersId(idTiers);
  }

  numberFilter(event: any): boolean {
    const inputChar = String.fromCharCode(event.charCode);
    if (!/^\d+$/.test(inputChar)) {
      event.preventDefault();
    }
    return true;
  }

  fillAffaire(entity: IndexEntity) {
    let obj = JSON.parse((entity.text as string));
    this.affaireService.getAffaire(entity.entityId).subscribe(affaire => {
      this.selectedAffaire = affaire;
    });
  }

  private loadComplainsByTiersId(idTiers: number) {
    this.salesComplainService.getComplainsByTiersId(idTiers).subscribe(complains => {
      if (Array.isArray(complains) && complains.length > 0)
      this.complainList = complains;
    });
  }

  submitForm(){
    if (this.selectedAffaire && this.selectedComplainCause && this.selectedComplainOrigin && this.selectedComplainProblem) {
    this.complain.affaire = this.selectedAffaire;
    this.complain.salesCause = this.selectedComplainCause;
    this.complain.salesProblem = this.selectedComplainProblem;
    this.complain.salesOrigin = this.selectedComplainOrigin;
    this.complain.idTiers = this.tiers.id;
    this.complain.responsableName = this.selectedEmployee?.firstname + " " + this.selectedEmployee?.lastname;
    this.complain.observations = this.observations;
    this.complain.customerOrderNumber = this.customerOrderNumber;
    let d = new Date();
    this.complain.complaintDate = new Date(d.getFullYear(), d.getMonth(), d.getDate(), d.getHours()+1, d.getUTCMinutes());
    this.salesComplainService.addOrUpdateComplain(this.complain).subscribe(response => {
      this.complain = response;
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


