import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { JeuneAgriculteurService } from 'src/app/modules/miscellaneous/services/guichet-unique/jeune.agriculteur.service';
import { JeuneAgriculteur } from 'src/app/modules/quotation/model/guichet-unique/referentials/JeuneAgriculteur';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-jeune-agriculteur',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupJeuneAgriculteurComponent extends GenericRadioGroupComponent<JeuneAgriculteur> implements OnInit {
  types: JeuneAgriculteur[] = [] as Array<JeuneAgriculteur>;

  constructor(
    private formBuild: UntypedFormBuilder, private JeuneAgriculteurService: JeuneAgriculteurService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.JeuneAgriculteurService.getJeuneAgriculteur().subscribe(response => { this.types = response })
  }
}
