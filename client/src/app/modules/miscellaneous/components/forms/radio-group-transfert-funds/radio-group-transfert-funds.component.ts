import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TransfertFundsType } from 'src/app/modules/quotation/model/TransfertFundsType';
import { TransfertFundsTypeService } from 'src/app/modules/quotation/services/transfert-funds-type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-transfert-funds',
  templateUrl: '../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTransfertFundsComponent extends GenericRadioGroupComponent<TransfertFundsType> implements OnInit {
  types: TransfertFundsType[] = [] as Array<TransfertFundsType>;

  constructor(
    private formBuild: UntypedFormBuilder, private transfertfundstypeService: TransfertFundsTypeService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.transfertfundstypeService.getTransfertFundsTypes().subscribe(response => { this.types = response })
  }
}
