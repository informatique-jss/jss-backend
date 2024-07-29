import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RegistreEirlDeLancienneEirlService } from 'src/app/modules/miscellaneous/services/guichet-unique/registre.eirl.de.lancienne.eirl.service';
import { RegistreEirlDeLancienneEirl } from '../../../../../quotation/model/guichet-unique/referentials/RegistreEirlDeLancienneEirl';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-registre-eirl-de-lancienne-eirl',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectRegistreEirlDeLancienneEirlComponent extends GenericSelectComponent<RegistreEirlDeLancienneEirl> implements OnInit {

  types: RegistreEirlDeLancienneEirl[] = [] as Array<RegistreEirlDeLancienneEirl>;

  constructor(private formBuild: UntypedFormBuilder, private RegistreEirlDeLancienneEirlService: RegistreEirlDeLancienneEirlService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.RegistreEirlDeLancienneEirlService.getRegistreEirlDeLancienneEirl().subscribe(response => {
      this.types = response;
    })
  }
}
