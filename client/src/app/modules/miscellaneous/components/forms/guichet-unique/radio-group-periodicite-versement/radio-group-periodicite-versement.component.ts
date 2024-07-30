import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PeriodiciteVersementService } from 'src/app/modules/miscellaneous/services/guichet-unique/periodicite.versement.service';
import { PeriodiciteVersement } from 'src/app/modules/quotation/model/guichet-unique/referentials/PeriodiciteVersement';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-periodicite-versement',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupPeriodiciteVersementComponent extends GenericRadioGroupComponent<PeriodiciteVersement> implements OnInit {
  types: PeriodiciteVersement[] = [] as Array<PeriodiciteVersement>;

  constructor(
    private formBuild: UntypedFormBuilder, private PeriodiciteVersementService: PeriodiciteVersementService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.PeriodiciteVersementService.getPeriodiciteVersement().subscribe(response => { this.types = response })
  }
}
