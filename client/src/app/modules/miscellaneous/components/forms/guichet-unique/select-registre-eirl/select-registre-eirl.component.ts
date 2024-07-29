import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RegistreEirlService } from 'src/app/modules/miscellaneous/services/guichet-unique/registre.eirl.service';
import { RegistreEirl } from '../../../../../quotation/model/guichet-unique/referentials/RegistreEirl';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-registre-eirl',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectRegistreEirlComponent extends GenericSelectComponent<RegistreEirl> implements OnInit {

  types: RegistreEirl[] = [] as Array<RegistreEirl>;

  constructor(private formBuild: UntypedFormBuilder, private RegistreEirlService: RegistreEirlService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.RegistreEirlService.getRegistreEirl().subscribe(response => {
      this.types = response;
    })
  }
}
