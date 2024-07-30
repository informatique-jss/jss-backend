import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { NatureDomaineService } from 'src/app/modules/miscellaneous/services/guichet-unique/nature.domaine.service';
import { NatureDomaine } from 'src/app/modules/quotation/model/guichet-unique/referentials/NatureDomaine';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-nature-domaine',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupNatureDomaineComponent extends GenericRadioGroupComponent<NatureDomaine> implements OnInit {
  types: NatureDomaine[] = [] as Array<NatureDomaine>;

  constructor(
    private formBuild: UntypedFormBuilder, private NatureDomaineService: NatureDomaineService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.NatureDomaineService.getNatureDomaine().subscribe(response => { this.types = response })
  }
}
