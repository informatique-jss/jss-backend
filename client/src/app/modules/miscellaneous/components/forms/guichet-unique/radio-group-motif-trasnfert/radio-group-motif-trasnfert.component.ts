import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MotifTrasnfertService } from 'src/app/modules/miscellaneous/services/guichet-unique/motif.trasnfert.service';
import { MotifTrasnfert } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifTrasnfert';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-motif-trasnfert',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupMotifTrasnfertComponent extends GenericRadioGroupComponent<MotifTrasnfert> implements OnInit {
  types: MotifTrasnfert[] = [] as Array<MotifTrasnfert>;

  constructor(
    private formBuild: UntypedFormBuilder, private MotifTrasnfertService: MotifTrasnfertService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.MotifTrasnfertService.getMotifTrasnfert().subscribe(response => { this.types = response })
  }
}
