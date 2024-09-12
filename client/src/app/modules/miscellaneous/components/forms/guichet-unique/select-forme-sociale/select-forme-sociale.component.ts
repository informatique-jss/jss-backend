import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { FormeSocialeService } from 'src/app/modules/miscellaneous/services/guichet-unique/forme.sociale.service';
import { FormeSociale } from '../../../../../quotation/model/guichet-unique/referentials/FormeSociale';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-forme-sociale',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectFormeSocialeComponent extends GenericSelectComponent<FormeSociale> implements OnInit {

  types: FormeSociale[] = [] as Array<FormeSociale>;

  constructor(private formBuild: UntypedFormBuilder, private FormeSocialeService: FormeSocialeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.FormeSocialeService.getFormeSociale().subscribe(response => {
      this.types = response;
    })
  }
}
