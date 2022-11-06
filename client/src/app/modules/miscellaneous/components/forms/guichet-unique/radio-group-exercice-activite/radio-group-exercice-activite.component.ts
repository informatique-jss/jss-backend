import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ExerciceActiviteService } from 'src/app/modules/miscellaneous/services/guichet-unique/exercice.activite.service';
import { ExerciceActivite } from 'src/app/modules/quotation/model/guichet-unique/referentials/ExerciceActivite';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-exercice-activite',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupExerciceActiviteComponent extends GenericRadioGroupComponent<ExerciceActivite> implements OnInit {
  types: ExerciceActivite[] = [] as Array<ExerciceActivite>;

  constructor(
    private formBuild: UntypedFormBuilder, private ExerciceActiviteService: ExerciceActiviteService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.ExerciceActiviteService.getExerciceActivite().subscribe(response => { this.types = response })
  }
}
