import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TutelleCuratelleService } from 'src/app/modules/miscellaneous/services/guichet-unique/tutelle.curatelle.service';
import { TutelleCuratelle } from '../../../../../quotation/model/guichet-unique/referentials/TutelleCuratelle';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-tutelle-curatelle',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectTutelleCuratelleComponent extends GenericSelectComponent<TutelleCuratelle> implements OnInit {

  types: TutelleCuratelle[] = [] as Array<TutelleCuratelle>;

  constructor(private formBuild: UntypedFormBuilder, private TutelleCuratelleService: TutelleCuratelleService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.TutelleCuratelleService.getTutelleCuratelle().subscribe(response => {
      this.types = response;
    })
  }
}
