import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MotifTrasnfertService } from 'src/app/modules/miscellaneous/services/guichet-unique/motif.trasnfert.service';
import { MotifTrasnfert } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifTrasnfert';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-motif-trasnfert',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupMotifTrasnfertComponent extends GenericRadioGroupComponent<MotifTrasnfert> implements OnInit {
  types: MotifTrasnfert[] = [] as Array<MotifTrasnfert>;

  constructor(
    private formBuild: UntypedFormBuilder, private MotifTrasnfertService: MotifTrasnfertService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.MotifTrasnfertService.getMotifTrasnfert().subscribe(response => { this.types = response })
  }
}
