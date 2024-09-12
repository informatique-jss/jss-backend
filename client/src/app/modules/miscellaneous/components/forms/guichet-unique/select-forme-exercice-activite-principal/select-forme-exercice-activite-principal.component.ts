import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { FormeExerciceActivitePrincipalService } from 'src/app/modules/miscellaneous/services/guichet-unique/forme.exercice.activite.principal.service';
import { FormeExerciceActivitePrincipal } from '../../../../../quotation/model/guichet-unique/referentials/FormeExerciceActivitePrincipal';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-forme-exercice-activite-principal',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectFormeExerciceActivitePrincipalComponent extends GenericSelectComponent<FormeExerciceActivitePrincipal> implements OnInit {

  types: FormeExerciceActivitePrincipal[] = [] as Array<FormeExerciceActivitePrincipal>;

  constructor(private formBuild: UntypedFormBuilder, private FormeExerciceActivitePrincipalService: FormeExerciceActivitePrincipalService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.FormeExerciceActivitePrincipalService.getFormeExerciceActivitePrincipal().subscribe(response => {
      this.types = response;
    })
  }
}
