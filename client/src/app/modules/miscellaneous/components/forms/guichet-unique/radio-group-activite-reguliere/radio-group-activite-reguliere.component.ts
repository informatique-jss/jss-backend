import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActiviteReguliereService } from 'src/app/modules/miscellaneous/services/guichet-unique/activite.reguliere.service';
import { ActiviteReguliere } from 'src/app/modules/quotation/model/guichet-unique/referentials/ActiviteReguliere';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-activite-reguliere',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupActiviteReguliereComponent extends GenericRadioGroupComponent<ActiviteReguliere> implements OnInit {
  types: ActiviteReguliere[] = [] as Array<ActiviteReguliere>;

  constructor(
    private formBuild: UntypedFormBuilder, private ActiviteReguliereService: ActiviteReguliereService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.ActiviteReguliereService.getActiviteReguliere().subscribe(response => { this.types = response })
  }
}
