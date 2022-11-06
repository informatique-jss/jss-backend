import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MotifRejetMsaService } from 'src/app/modules/miscellaneous/services/guichet-unique/motif.rejet.msa.service';
import { MotifRejetMsa } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifRejetMsa';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-motif-rejet-msa',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupMotifRejetMsaComponent extends GenericRadioGroupComponent<MotifRejetMsa> implements OnInit {
  types: MotifRejetMsa[] = [] as Array<MotifRejetMsa>;

  constructor(
    private formBuild: UntypedFormBuilder, private MotifRejetMsaService: MotifRejetMsaService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.MotifRejetMsaService.getMotifRejetMsa().subscribe(response => { this.types = response })
  }
}
