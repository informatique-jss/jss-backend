import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PeriodiciteEtOptionsParticulieService } from 'src/app/modules/miscellaneous/services/guichet-unique/periodicite.et.options.particulie.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { PeriodiciteEtOptionsParticulie } from '../../../../../quotation/model/guichet-unique/referentials/PeriodiciteEtOptionsParticulie';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-periodicite-et-options-particulie',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectPeriodiciteEtOptionsParticulieComponent extends GenericSelectComponent<PeriodiciteEtOptionsParticulie> implements OnInit {

  types: PeriodiciteEtOptionsParticulie[] = [] as Array<PeriodiciteEtOptionsParticulie>;

  constructor(private formBuild: UntypedFormBuilder, private PeriodiciteEtOptionsParticulieService: PeriodiciteEtOptionsParticulieService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.PeriodiciteEtOptionsParticulieService.getPeriodiciteEtOptionsParticulie().subscribe(response => {
      this.types = response;
    })
  }
}
