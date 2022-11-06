import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeLiasseService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.liasse.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { TypeLiasse } from '../../../../../quotation/model/guichet-unique/referentials/TypeLiasse';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-type-liasse',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectTypeLiasseComponent extends GenericSelectComponent<TypeLiasse> implements OnInit {

  types: TypeLiasse[] = [] as Array<TypeLiasse>;

  constructor(private formBuild: UntypedFormBuilder, private TypeLiasseService: TypeLiasseService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.TypeLiasseService.getTypeLiasse().subscribe(response => {
      this.types = response;
    })
  }
}
