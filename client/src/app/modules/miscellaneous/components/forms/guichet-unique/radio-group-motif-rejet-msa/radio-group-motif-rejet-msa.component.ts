import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MotifRejetMsaService } from 'src/app/modules/miscellaneous/services/guichet-unique/motif.rejet.msa.service';
import { MotifRejetMsa } from 'src/app/modules/quotation/model/guichet-unique/referentials/MotifRejetMsa';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-motif-rejet-msa',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupMotifRejetMsaComponent extends GenericRadioGroupComponent<MotifRejetMsa> implements OnInit {
  types: MotifRejetMsa[] = [] as Array<MotifRejetMsa>;

  constructor(
    private formBuild: UntypedFormBuilder, private MotifRejetMsaService: MotifRejetMsaService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.MotifRejetMsaService.getMotifRejetMsa().subscribe(response => { this.types = response })
  }
}
