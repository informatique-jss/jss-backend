import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute, Router } from '@angular/router';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { AppService } from 'src/app/services/app.service';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { Provision } from '../../model/Provision';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';

@Component({
  selector: 'affaire',
  templateUrl: './affaire.component.html',
  styleUrls: ['./affaire.component.css']
})
export class AffaireComponent implements OnInit {

  idAffaire: number | undefined;
  asso: AssoAffaireOrder = {} as AssoAffaireOrder;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;
  editMode: boolean = false;
  isStatusOpen: boolean = false;
  inputProvisionId: number = 0;

  constructor(
    private activatedRoute: ActivatedRoute,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private router: Router,
  ) { }

  affaireForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Affaire");
    this.idAffaire = this.activatedRoute.snapshot.params.id;
    this.inputProvisionId = this.activatedRoute.snapshot.params.idProvision;
    if (this.idAffaire)
      this.assoAffaireOrderService.getAssoAffaireOrder(this.idAffaire).subscribe(response => {
        this.asso = response;
        if (this.asso.affaire)
          this.appService.changeHeaderTitle("Affaire " + (this.asso.affaire.denomination ? this.asso.affaire.denomination : (this.asso.affaire.firstname + " " + this.asso.affaire.lastname)));
      })
  }

  updateAssignedToForAffaire(employee: Employee, asso: AssoAffaireOrder) {
    this.assoAffaireOrderService.updateAssignedToForAsso(asso, employee).subscribe(response => {
    });
  }

  deleteProvision(asso: AssoAffaireOrder, provision: Provision) {
    asso.provisions.splice(asso.provisions.indexOf(provision), 1);
  }


  createProvision(asso: AssoAffaireOrder): Provision {
    if (asso && !asso.provisions)
      asso.provisions = [] as Array<Provision>;
    let provision = {} as Provision;
    asso.provisions.push(provision);
    return provision;
  }

  editAsso() {
    this.editMode = true;
  }

  saveAsso() {
    if (this.affaireForm.valid) {
      this.assoAffaireOrderService.updateAsso(this.asso).subscribe(response => {
        this.asso = response;
        this.editMode = false;
        this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
          this.router.navigate(['/affaire/', "" + this.idAffaire])
        );
      })
    } else {
      this.appService.displaySnackBar("Les onglets suivants ne sont pas correctement remplis. Veuillez les compléter avant de sauvegarder : Prestations", true, 60);
    }
  }

  displayQuotation() {
    this.router.navigate(['/quotation/', "" + this.asso.quotation.id])
  }

  displayCustomerOrder() {
    this.router.navigate(['/order/', "" + this.asso.customerOrder.id])
  }

}
