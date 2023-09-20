import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY } from 'src/app/libs/Constants';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { FORMALITE_ENTITY_TYPE, PROVISION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { ConstantService } from '../../../miscellaneous/services/constant.service';
import { Formalite } from '../../model/Formalite';
import { IQuotation } from '../../model/IQuotation';
import { Provision } from '../../model/Provision';

@Component({
  selector: 'formalite',
  templateUrl: './formalite.component.html',
  styleUrls: ['./formalite.component.css']
})
export class FormaliteComponent implements OnInit {

  @Input() formalite: Formalite = {} as Formalite;
  @Input() provision: Provision | undefined;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Input() quotation: IQuotation | undefined;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();

  FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY = FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY;
  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;
  instanceOfCustomerOrderFn = instanceOfCustomerOrder;

  competentAuthorityInpi = this.constantService.getCompetentAuthorityInpi();
  competentAuthorityInfogreffe = this.constantService.getCompetentAuthorityInfogreffe();

  constructor(
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
  ) { }

  formaliteForm = this.formBuilder.group({});

  FORMALITE_ENTITY_TYPE = FORMALITE_ENTITY_TYPE;

  ngOnInit() {
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

}
