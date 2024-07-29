import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ModalitesDeControleService } from 'src/app/modules/miscellaneous/services/guichet-unique/modalites.de.controle.service';
import { ModalitesDeControle } from '../../../../../quotation/model/guichet-unique/referentials/ModalitesDeControle';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-modalites-de-controle',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectModalitesDeControleComponent extends GenericSelectComponent<ModalitesDeControle> implements OnInit {

  types: ModalitesDeControle[] = [] as Array<ModalitesDeControle>;

  constructor(private formBuild: UntypedFormBuilder, private ModalitesDeControleService: ModalitesDeControleService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.ModalitesDeControleService.getModalitesDeControle().subscribe(response => {
      this.types = response;
    })
  }
}
