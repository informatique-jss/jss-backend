import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutDomaineService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.domaine.service';
import { StatutDomaine } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutDomaine';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-statut-domaine',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupStatutDomaineComponent extends GenericRadioGroupComponent<StatutDomaine> implements OnInit {
  types: StatutDomaine[] = [] as Array<StatutDomaine>;

  constructor(
    private formBuild: UntypedFormBuilder, private StatutDomaineService: StatutDomaineService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.StatutDomaineService.getStatutDomaine().subscribe(response => { this.types = response })
  }
}
