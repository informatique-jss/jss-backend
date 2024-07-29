import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PeriodiciteEtOptionsParticulieService } from 'src/app/modules/miscellaneous/services/guichet-unique/periodicite.et.options.particulie.service';
import { PeriodiciteEtOptionsParticulie } from '../../../../../quotation/model/guichet-unique/referentials/PeriodiciteEtOptionsParticulie';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-periodicite-et-options-particulie',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectPeriodiciteEtOptionsParticulieComponent extends GenericSelectComponent<PeriodiciteEtOptionsParticulie> implements OnInit {

  types: PeriodiciteEtOptionsParticulie[] = [] as Array<PeriodiciteEtOptionsParticulie>;

  constructor(private formBuild: UntypedFormBuilder, private PeriodiciteEtOptionsParticulieService: PeriodiciteEtOptionsParticulieService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.PeriodiciteEtOptionsParticulieService.getPeriodiciteEtOptionsParticulie().subscribe(response => {
      this.types = response;
    })
  }
}
