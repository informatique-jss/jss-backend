import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { PROVISION_TYPE_DOMICILIATION_CODE, PROVISION_TYPE_SHAL_CODE } from 'src/app/libs/Constants';
import { Provision } from '../../model/Provision';
import { ProvisionFamilyType } from '../../model/ProvisionFamilyType';
import { ProvisionType } from '../../model/ProvisionType';
import { ProvisionFamilyTypeService } from '../../services/provision.family.type.service';
import { ProvisionTypeService } from '../../services/provision.type.service';
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
  @Input() editMode: boolean = false;
  @Output() deleteEvent = new EventEmitter();
  @ViewChild(DomiciliationComponent) domiciliationComponent: DomiciliationComponent | undefined;
  @ViewChild(ShalComponent) shalComponent: ShalComponent | undefined;

  provisionFamilyTypes: ProvisionFamilyType[] = [] as Array<ProvisionFamilyType>;
  provisionTypes: ProvisionType[] = [] as Array<ProvisionType>;

  PROVISION_TYPE_DOMICILIATION_CODE = PROVISION_TYPE_DOMICILIATION_CODE;
  PROVISION_TYPE_SHAL_CODE = PROVISION_TYPE_SHAL_CODE;

  constructor(private formBuilder: FormBuilder,
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
    if (changes.provisions != undefined) {
      this.provisionForm.markAllAsTouched();
    }
  }

  getFormStatus(): boolean {
    let status = true;
    if (this.domiciliationComponent)
      status = status && this.domiciliationComponent.getFormStatus();

    if (this.shalComponent)
      status = status && this.shalComponent.getFormStatus();
    return status && this.provisionForm.valid;
  }

  provisionForm = this.formBuilder.group({
    provisionType: ['', [Validators.required]],
    provisionFamilyType: ['', [Validators.required]],
  });

  deleteProvision(provision: Provision) {
    this.deleteEvent.emit(provision);
  }

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
      return;
    }

    if (this.provision.provisionType.code != PROVISION_TYPE_DOMICILIATION_CODE)
      this.provision.domiciliation = undefined;

    if (this.provision.provisionType.code != PROVISION_TYPE_SHAL_CODE)
      this.provision.shal = undefined;
  }

  compareWithId = compareWithId;

}
