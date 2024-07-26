import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MotifDisparitionService } from 'src/app/modules/miscellaneous/services/guichet-unique/motif.disparition.service';
import { MotifDisparition } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifDisparition';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-motif-disparition',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupMotifDisparitionComponent extends GenericRadioGroupComponent<MotifDisparition> implements OnInit {
  types: MotifDisparition[] = [] as Array<MotifDisparition>;

  constructor(
    private formBuild: UntypedFormBuilder, private MotifDisparitionService: MotifDisparitionService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.MotifDisparitionService.getMotifDisparition().subscribe(response => { this.types = response })
  }
}
