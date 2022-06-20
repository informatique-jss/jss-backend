import { Component, Input, OnInit, SimpleChanges, ViewChild, ViewChildren } from '@angular/core';
import { MatAccordion } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { PROVISION_TYPE_DOMICILIATION_CODE, PROVISION_TYPE_SHAL_CODE } from 'src/app/libs/Constants';
import { Affaire } from '../../model/Affaire';
import { Provision } from '../../model/Provision';
import { AffaireComponent } from '../affaire/affaire.component';
import { ProvisionItemComponent } from '../provision-item/provision-item.component';

@Component({
  selector: 'provision',
  templateUrl: './provision.component.html',
  styleUrls: ['./provision.component.css']
})
export class ProvisionComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() provisions: Provision[] = [] as Array<Provision>;
  @Input() editMode: boolean = false;
  @ViewChildren(ProvisionItemComponent) provisionItemComponents: any;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;
  @ViewChild(AffaireComponent) affaireComponent: AffaireComponent | undefined;

  filteredProvisions: Provision[] = [] as Array<Provision>;

  PROVISION_TYPE_DOMICILIATION_CODE = PROVISION_TYPE_DOMICILIATION_CODE;
  PROVISION_TYPE_SHAL_CODE = PROVISION_TYPE_SHAL_CODE;

  constructor(
  ) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.provisions != undefined) {
      if (this.provisions != null && this.provisions != undefined && this.provisions.length > 0) {
        this.sortProvisions();
        this.filteredProvisions = this.provisions;
      }
      this.applyFilter(null);
    }
  }

  sortProvisions() {
    this.provisions.sort((a: Provision, b: Provision) => {
      if (a.affaire == null && b.affaire != null)
        return -1;
      if (b.affaire != null && b.affaire == null)
        return 1;
      if (a.affaire.id == null && b.affaire.id != null)
        return -1;
      if (b.affaire.id != null && b.affaire.id == null)
        return 1;
      if (a.affaire == null && b.affaire == null)
        return 0;
      let nameA = "";
      let nameB = "";
      if (a.affaire.isIndividual) {
        nameA = (a.affaire.firstname != null ? a.affaire.firstname : "") + (a.affaire.lastname != null ? a.affaire.lastname : "");
      } else {
        nameA = a.affaire.denomination;
      }
      if (b.affaire.isIndividual) {
        nameB = (b.affaire.firstname != null ? b.affaire.firstname : "") + (b.affaire.lastname != null ? b.affaire.lastname : "");
      } else {
        nameB = b.affaire.denomination;
      }
      return nameA.localeCompare(nameB);
    })
  }

  applyFilter(filterValue: any) {
    if (filterValue == null || filterValue == undefined || filterValue.length == 0) {
      this.filteredProvisions = this.provisions;
      return;
    }
    this.filteredProvisions = [] as Array<Provision>;
    if (this.provisions != null && this.provisions != undefined) {
      this.provisions.forEach(provision => {
        const dataStr = JSON.stringify(provision).toLowerCase();
        if (dataStr.indexOf(filterValue.value.toLowerCase()) >= 0)
          this.filteredProvisions.push(provision);
      })
    }
  }

  createProvision() {
    if (this.provisions == null || this.provisions == undefined)
      this.provisions = [] as Array<Provision>;
    let provision = {} as Provision;
    provision.affaire = {} as Affaire;
    this.provisions.push(provision);
    this.sortProvisions();
    this.applyFilter(null);
  }

  deleteProvision(index: number) {
    if (this.filteredProvisions != null && this.filteredProvisions != undefined && this.filteredProvisions.length > 0) {
      for (let i = 0; i < this.provisions.length; i++) {
        const provision = this.provisions[i];
        if (this.sameProvision(provision, this.filteredProvisions[index]))
          this.provisions.splice(i, 1);
      }
    }
    this.applyFilter(null);
  }

  sameProvision(p1: Provision, p2: Provision): boolean {
    return JSON.stringify(p1).toLowerCase() == JSON.stringify(p2).toLowerCase();
  }

  getFormStatus(): boolean {
    let status = true;
    let affaireFormStatus = this.affaireComponent?.getFormStatus();
    this.provisionItemComponents.toArray().forEach((provisionComponent: { getFormStatus: () => any; }) => {
      status = status && provisionComponent.getFormStatus();
    });
    if (affaireFormStatus)
      status = status && affaireFormStatus;
    return status;
  }

  compareWithId = compareWithId;

}

