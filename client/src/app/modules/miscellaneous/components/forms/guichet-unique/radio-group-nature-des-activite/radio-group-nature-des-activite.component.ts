import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { NatureDesActiviteService } from 'src/app/modules/miscellaneous/services/guichet-unique/nature.des.activite.service';
import { NatureDesActivite } from 'src/app/modules/quotation/model/guichet-unique/referentials/NatureDesActivite';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-nature-des-activite',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupNatureDesActiviteComponent extends GenericRadioGroupComponent<NatureDesActivite> implements OnInit {
  types: NatureDesActivite[] = [] as Array<NatureDesActivite>;

  constructor(
    private formBuild: UntypedFormBuilder, private NatureDesActiviteService: NatureDesActiviteService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.NatureDesActiviteService.getNatureDesActivite().subscribe(response => { this.types = response })
  }
}
