import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeRepresentantService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.representant.service';
import { TypeRepresentant } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeRepresentant';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-type-representant',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTypeRepresentantComponent extends GenericRadioGroupComponent<TypeRepresentant> implements OnInit {
  types: TypeRepresentant[] = [] as Array<TypeRepresentant>;

  constructor(
    private formBuild: UntypedFormBuilder, private TypeRepresentantService: TypeRepresentantService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.TypeRepresentantService.getTypeRepresentant().subscribe(response => { this.types = response })
  }
}
