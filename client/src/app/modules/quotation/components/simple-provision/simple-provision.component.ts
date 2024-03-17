import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY } from 'src/app/libs/Constants';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { PROVISION_ENTITY_TYPE, SIMPLE_PROVISION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { instanceOfCustomerOrder } from '../../../../libs/TypeHelper';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { IQuotation } from '../../model/IQuotation';
import { Provision } from '../../model/Provision';
import { SimpleProvision } from '../../model/SimpleProvision';
import { SimpleProvisionStatus } from '../../model/SimpleProvisonStatus';
import { SimpleProvisionStatusService } from '../../services/simple.provision.status.service';

@Component({
  selector: 'simple-provision',
  templateUrl: './simple-provision.component.html',
  styleUrls: ['./simple-provision.component.css']
})
export class SimpleProvisionComponent implements OnInit {


  @Input() simpleProvision: SimpleProvision = {} as SimpleProvision;
  @Input() provision: Provision | undefined;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Input() quotation: IQuotation | undefined;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();

  SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY = SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY;
  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;

  simpleProvisionStatus: SimpleProvisionStatus[] | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private habilitationsService: HabilitationsService,
    private simpleProvisionStatusService: SimpleProvisionStatusService,
    private userPreferenceService: UserPreferenceService
  ) { }

  canAddNewInvoice() {
    return this.habilitationsService.canAddNewInvoice();
  }
  simpleProvisionForm = this.formBuilder.group({});

  SIMPLE_PROVISION_ENTITY_TYPE = SIMPLE_PROVISION_ENTITY_TYPE;
  instanceOfCustomerOrderFn = instanceOfCustomerOrder;

  ngOnInit() {
    this.simpleProvisionStatusService.getSimpleProvisionStatus().subscribe(response => this.simpleProvisionStatus = response);
    this.restoreTab();
  }

  ngOnChanges(changes: SimpleChanges) {

  }

  getFormStatus() {
    this.simpleProvisionForm.markAllAsTouched();
    let status = this.simpleProvisionForm.valid;

    return status;
  }

  provisionChangeFunction() {
    this.provisionChange.emit(this.provision);
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('simple-provision', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('simple-provision');
  }
}
