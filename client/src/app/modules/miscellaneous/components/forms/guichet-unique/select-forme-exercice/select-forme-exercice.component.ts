import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { FormeExerciceService } from 'src/app/modules/miscellaneous/services/guichet-unique/forme.exercice.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { FormeExercice } from '../../../../../quotation/model/guichet-unique/referentials/FormeExercice';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-forme-exercice',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectFormeExerciceComponent extends GenericSelectComponent<FormeExercice> implements OnInit {

  types: FormeExercice[] = [] as Array<FormeExercice>;

  constructor(private formBuild: UntypedFormBuilder, private FormeExerciceService: FormeExerciceService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.FormeExerciceService.getFormeExercice().subscribe(response => {
      this.types = response;
    })
  }
}
