import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { OrigineFusionScissionService } from 'src/app/modules/miscellaneous/services/guichet-unique/origine.fusion.scission.service';
import { OrigineFusionScission } from 'src/app/modules/quotation/model/guichet-unique/referentials/OrigineFusionScission';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-origine-fusion-scission',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupOrigineFusionScissionComponent extends GenericRadioGroupComponent<OrigineFusionScission> implements OnInit {
  types: OrigineFusionScission[] = [] as Array<OrigineFusionScission>;

  constructor(
    private formBuild: UntypedFormBuilder, private OrigineFusionScissionService: OrigineFusionScissionService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.OrigineFusionScissionService.getOrigineFusionScission().subscribe(response => { this.types = response })
  }
}
