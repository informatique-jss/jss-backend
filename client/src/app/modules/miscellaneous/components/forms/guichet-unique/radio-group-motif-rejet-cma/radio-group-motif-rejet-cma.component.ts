import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MotifRejetCmaService } from 'src/app/modules/miscellaneous/services/guichet-unique/motif.rejet.cma.service';
import { MotifRejetCma } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifRejetCma';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-motif-rejet-cma',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupMotifRejetCmaComponent extends GenericRadioGroupComponent<MotifRejetCma> implements OnInit {
  types: MotifRejetCma[] = [] as Array<MotifRejetCma>;

  constructor(
    private formBuild: UntypedFormBuilder, private MotifRejetCmaService: MotifRejetCmaService,) {
    super(formBuild);
  }

  initTypes(): void {
    this.MotifRejetCmaService.getMotifRejetCma().subscribe(response => { this.types = response })
  }
}
