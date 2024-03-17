import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY, GUICHET_UNIQUE_STATUS_AMENDMENT_PENDING, GUICHET_UNIQUE_STATUS_AMENDMENT_SIGNATURE_PENDING } from 'src/app/libs/Constants';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { FORMALITE_ENTITY_TYPE, PROVISION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { ConstantService } from '../../../miscellaneous/services/constant.service';
import { Affaire } from '../../model/Affaire';
import { Formalite } from '../../model/Formalite';
import { FormaliteStatus } from '../../model/FormaliteStatus';
import { IQuotation } from '../../model/IQuotation';
import { Provision } from '../../model/Provision';
import { FormaliteStatusService } from '../../services/formalite.status.service';

@Component({
  selector: 'formalite',
  templateUrl: './formalite.component.html',
  styleUrls: ['./formalite.component.css']
})
export class FormaliteComponent implements OnInit {

  @Input() formalite: Formalite = {} as Formalite;
  @Input() provision: Provision | undefined;
  @Input() affaire: Affaire | undefined;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Input() quotation: IQuotation | undefined;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();

  FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY = FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY;
  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;
  GUICHET_UNIQUE_STATUS_AMENDMENT_PENDING = GUICHET_UNIQUE_STATUS_AMENDMENT_PENDING;
  GUICHET_UNIQUE_STATUS_AMENDMENT_SIGNATURE_PENDING = GUICHET_UNIQUE_STATUS_AMENDMENT_SIGNATURE_PENDING;
  instanceOfCustomerOrderFn = instanceOfCustomerOrder;

  competentAuthorityInpi = this.constantService.getCompetentAuthorityInpi();
  competentAuthorityInfogreffe = this.constantService.getCompetentAuthorityInfogreffe();

  formaliteStatus: FormaliteStatus[] | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private habilitationsService: HabilitationsService,
    private formaliteStatusService: FormaliteStatusService,
    private userPreferenceService: UserPreferenceService
  ) { }

  formaliteForm = this.formBuilder.group({});
  canAddNewInvoice() {
    return this.habilitationsService.canAddNewInvoice();
  }
  FORMALITE_ENTITY_TYPE = FORMALITE_ENTITY_TYPE;

  ngOnInit() {
    this.formaliteStatusService.getFormaliteStatus().subscribe(response => { this.formaliteStatus = response });
    this.restoreTab();
  }

  ngOnChanges(changes: SimpleChanges) {

  }

  getFormStatus() {
    this.formaliteForm.markAllAsTouched();
    let status = this.formaliteForm.valid;

    return status;
  }

  provisionChangeFunction() {
    this.provisionChange.emit(this.provision);
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('formalite', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('formalite');
  }
}
