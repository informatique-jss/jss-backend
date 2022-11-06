import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeDissolutionService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.dissolution.service';
import { TypeDissolution } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDissolution';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-type-dissolution',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTypeDissolutionComponent extends GenericRadioGroupComponent<TypeDissolution> implements OnInit {
  types: TypeDissolution[] = [] as Array<TypeDissolution>;

  constructor(
    private formBuild: UntypedFormBuilder, private TypeDissolutionService: TypeDissolutionService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.TypeDissolutionService.getTypeDissolution().subscribe(response => { this.types = response })
  }
}
