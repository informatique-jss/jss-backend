import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeFormaliteService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.formalite.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { TypeFormalite } from '../../../../../quotation/model/guichet-unique/referentials/TypeFormalite';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-type-formalite',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectTypeFormaliteComponent extends GenericSelectComponent<TypeFormalite> implements OnInit {

  types: TypeFormalite[] = [] as Array<TypeFormalite>;

  constructor(private formBuild: UntypedFormBuilder, private TypeFormaliteService: TypeFormaliteService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.TypeFormaliteService.getTypeFormalite().subscribe(response => {
      this.types = response;
    })
  }
}
