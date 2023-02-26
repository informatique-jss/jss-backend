import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY } from 'src/app/libs/Constants';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { PROVISION_ENTITY_TYPE, SIMPLE_PROVISION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { Provision } from '../../model/Provision';
import { SimpleProvision } from '../../model/SimpleProvision';

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
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();

  SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY = SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY;
  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;

  constructor(
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
  ) { }

  simpleProvisionForm = this.formBuilder.group({});

  SIMPLE_PROVISION_ENTITY_TYPE = SIMPLE_PROVISION_ENTITY_TYPE;

  ngOnInit() {
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

}
