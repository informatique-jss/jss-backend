import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypePersonneService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.personne.service';
import { TypePersonne } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypePersonne';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-type-personne',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTypePersonneComponent extends GenericRadioGroupComponent<TypePersonne> implements OnInit {
  types: TypePersonne[] = [] as Array<TypePersonne>;

  constructor(
    private formBuild: UntypedFormBuilder, private TypePersonneService: TypePersonneService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.TypePersonneService.getTypePersonne().subscribe(response => { this.types = response })
  }
}
