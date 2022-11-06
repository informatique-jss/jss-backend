import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeDeStatutsService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.de.statuts.service';
import { TypeDeStatuts } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDeStatuts';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-type-de-statuts',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTypeDeStatutsComponent extends GenericRadioGroupComponent<TypeDeStatuts> implements OnInit {
  types: TypeDeStatuts[] = [] as Array<TypeDeStatuts>;

  constructor(
    private formBuild: UntypedFormBuilder, private TypeDeStatutsService: TypeDeStatutsService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.TypeDeStatutsService.getTypeDeStatuts().subscribe(response => { this.types = response })
  }
}
