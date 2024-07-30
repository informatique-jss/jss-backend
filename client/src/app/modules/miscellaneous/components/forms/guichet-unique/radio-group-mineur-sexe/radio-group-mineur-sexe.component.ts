import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MineurSexeService } from 'src/app/modules/miscellaneous/services/guichet-unique/mineur.sexe.service';
import { MineurSexe } from 'src/app/modules/quotation/model/guichet-unique/referentials/MineurSexe';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-mineur-sexe',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupMineurSexeComponent extends GenericRadioGroupComponent<MineurSexe> implements OnInit {
  types: MineurSexe[] = [] as Array<MineurSexe>;

  constructor(
    private formBuild: UntypedFormBuilder, private MineurSexeService: MineurSexeService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.MineurSexeService.getMineurSexe().subscribe(response => { this.types = response })
  }
}
