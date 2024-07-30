import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutDomaineService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.domaine.service';
import { StatutDomaine } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutDomaine';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-statut-domaine',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupStatutDomaineComponent extends GenericRadioGroupComponent<StatutDomaine> implements OnInit {
  types: StatutDomaine[] = [] as Array<StatutDomaine>;

  constructor(
    private formBuild: UntypedFormBuilder, private StatutDomaineService: StatutDomaineService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.StatutDomaineService.getStatutDomaine().subscribe(response => { this.types = response })
  }
}
