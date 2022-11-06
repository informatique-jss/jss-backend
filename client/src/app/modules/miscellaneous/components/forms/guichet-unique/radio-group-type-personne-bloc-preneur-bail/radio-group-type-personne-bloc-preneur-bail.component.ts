import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypePersonneBlocPreneurBailService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.personne.bloc.preneur.bail.service';
import { TypePersonneBlocPreneurBail } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypePersonneBlocPreneurBail';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-type-personne-bloc-preneur-bail',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTypePersonneBlocPreneurBailComponent extends GenericRadioGroupComponent<TypePersonneBlocPreneurBail> implements OnInit {
  types: TypePersonneBlocPreneurBail[] = [] as Array<TypePersonneBlocPreneurBail>;

  constructor(
    private formBuild: UntypedFormBuilder, private TypePersonneBlocPreneurBailService: TypePersonneBlocPreneurBailService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.TypePersonneBlocPreneurBailService.getTypePersonneBlocPreneurBail().subscribe(response => { this.types = response })
  }
}
