import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { PROVISION_TYPE_BODACC_CODE, PROVISION_TYPE_DOMICILIATION_CODE, PROVISION_TYPE_SHAL_CODE } from 'src/app/libs/Constants';
import { Affaire } from '../../model/Affaire';
import { Bodacc } from '../../model/Bodacc';
import { Domiciliation } from '../../model/Domiciliation';
import { Provision } from '../../model/Provision';
import { ProvisionFamilyType } from '../../model/ProvisionFamilyType';
import { ProvisionType } from '../../model/ProvisionType';
import { Shal } from '../../model/Shal';
import { ProvisionFamilyTypeService } from '../../services/provision.family.type.service';
import { ProvisionTypeService } from '../../services/provision.type.service';
import { BodaccMainComponent } from '../bodacc-main/bodacc-main.component';
import { DomiciliationComponent } from '../domiciliation/domiciliation.component';
import { ShalComponent } from '../shal/shal.component';


@Component({
  selector: 'provision-item',
  templateUrl: './provision-item.component.html',
  styleUrls: ['./provision-item.component.css'],
  viewProviders: [MatExpansionPanel]
})
export class ProvisionItemComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() provision: Provision = {} as Provision;
  @Input() affaire: Affaire = {} as Affaire;
  @Input() editMode: boolean = false;
  @ViewChild(DomiciliationComponent) domiciliationComponent: DomiciliationComponent | undefined;
  @ViewChild(ShalComponent) shalComponent: ShalComponent | undefined;
  @ViewChild(ShalComponent) bodaccComponent: BodaccMainComponent | undefined;

  provisionFamilyTypes: ProvisionFamilyType[] = [] as Array<ProvisionFamilyType>;
  provisionTypes: ProvisionType[] = [] as Array<ProvisionType>;

  PROVISION_TYPE_DOMICILIATION_CODE = PROVISION_TYPE_DOMICILIATION_CODE;
  PROVISION_TYPE_SHAL_CODE = PROVISION_TYPE_SHAL_CODE;
  PROVISION_TYPE_BODACC_CODE = PROVISION_TYPE_BODACC_CODE;

  constructor(private formBuilder: UntypedFormBuilder,
    protected provisionFamilyTypeService: ProvisionFamilyTypeService,
    protected provisionTypeService: ProvisionTypeService,
  ) { }

  ngOnInit() {
    this.provisionFamilyTypeService.getProvisionFamilyTypes().subscribe(response => {
      this.provisionFamilyTypes = response;
    })
    this.provisionTypeService.getProvisionTypes().subscribe(response => {
      this.provisionTypes = response;
    })
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.provision != undefined) {
      console.log(this.provision);
      this.provisionItemForm.markAllAsTouched();
    }
  }

  getFormStatus(): boolean {
    let status = true;
    if (this.domiciliationComponent)
      status = status && this.domiciliationComponent.getFormStatus();

    if (this.shalComponent)
      status = status && this.shalComponent.getFormStatus();

    if (this.bodaccComponent)
      status = status && this.bodaccComponent.getFormStatus();
    return status && this.provisionItemForm.valid;
  }

  provisionItemForm = this.formBuilder.group({
    provisionType: ['', [Validators.required]],
    provisionFamilyType: ['', [Validators.required]],
  });

  changeProvisionType() {
    if (this.provision.provisionFamilyType) {
      for (let provisionType of this.provisionTypes) {
        if (provisionType.label == this.provision.provisionFamilyType.label)
          this.provision.provisionType = provisionType;
      }
    }

    // TODO : add formalité or bodacc
    if (!this.provision.provisionFamilyType || !this.provision.provisionType) {
      this.provision.shal = undefined;
      this.provision.domiciliation = undefined;
      this.provision.bodacc = undefined;
      return;
    }

    if (this.provision.provisionType.code != PROVISION_TYPE_DOMICILIATION_CODE) {
      this.provision.domiciliation = undefined;
    } else if (!this.provision.domiciliation) {
      this.provision.domiciliation = {} as Domiciliation;
    }

    if (this.provision.provisionType.code != PROVISION_TYPE_SHAL_CODE) {
      this.provision.shal = undefined;
    } else if (!this.provision.shal) {
      this.provision.shal = {} as Shal;
    }

    if (this.provision.provisionType.code != PROVISION_TYPE_BODACC_CODE) {
      this.provision.bodacc = undefined;
    } else if (!this.provision.bodacc) {
      this.provision.bodacc = {} as Bodacc;
    }
  }

  compareWithId = compareWithId;

}
