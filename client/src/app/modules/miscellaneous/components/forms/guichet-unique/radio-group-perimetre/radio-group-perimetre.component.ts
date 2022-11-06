import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PerimetreService } from 'src/app/modules/miscellaneous/services/guichet-unique/perimetre.service';
import { Perimetre } from 'src/app/modules/quotation/model/guichet-unique/referentials/Perimetre';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-perimetre',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupPerimetreComponent extends GenericRadioGroupComponent<Perimetre> implements OnInit {
  types: Perimetre[] = [] as Array<Perimetre>;

  constructor(
    private formBuild: UntypedFormBuilder, private PerimetreService: PerimetreService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.PerimetreService.getPerimetre().subscribe(response => { this.types = response })
  }
}
