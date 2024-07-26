import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutExerciceActiviteSimultanService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.exercice.activite.simultan.service';
import { StatutExerciceActiviteSimultan } from '../../../../../quotation/model/guichet-unique/referentials/StatutExerciceActiviteSimultan';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-statut-exercice-activite-simultan',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectStatutExerciceActiviteSimultanComponent extends GenericSelectComponent<StatutExerciceActiviteSimultan> implements OnInit {

  types: StatutExerciceActiviteSimultan[] = [] as Array<StatutExerciceActiviteSimultan>;

  constructor(private formBuild: UntypedFormBuilder, private StatutExerciceActiviteSimultanService: StatutExerciceActiviteSimultanService,) {
    super(formBuild,)
  }

  initTypes(): void {
    this.StatutExerciceActiviteSimultanService.getStatutExerciceActiviteSimultan().subscribe(response => {
      this.types = response;
    })
  }
}
