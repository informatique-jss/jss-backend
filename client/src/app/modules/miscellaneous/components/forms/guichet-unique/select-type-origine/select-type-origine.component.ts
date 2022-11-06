import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeOrigineService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.origine.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { TypeOrigine } from '../../../../../quotation/model/guichet-unique/referentials/TypeOrigine';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-type-origine',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectTypeOrigineComponent extends GenericSelectComponent<TypeOrigine> implements OnInit {

  types: TypeOrigine[] = [] as Array<TypeOrigine>;

  constructor(private formBuild: UntypedFormBuilder, private TypeOrigineService: TypeOrigineService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.TypeOrigineService.getTypeOrigine().subscribe(response => {
      this.types = response;
    })
  }
}
