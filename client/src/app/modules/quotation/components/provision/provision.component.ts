import { Component, Input, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { MatAccordion } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { PROVISION_TYPE_DOMICILIATION_CODE, PROVISION_TYPE_SHAL_CODE } from 'src/app/libs/Constants';
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


  getFormStatus(): boolean {
    let status = true;
    let affaireFormStatus = this.affaireComponent?.getFormStatus();

    if (affaireFormStatus)
      status = status && affaireFormStatus;
    return status;
  }

  compareWithId = compareWithId;

}

