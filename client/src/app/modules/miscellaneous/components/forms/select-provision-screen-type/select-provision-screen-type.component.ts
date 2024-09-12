import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ProvisionScreenType } from 'src/app/modules/quotation/model/ProvisionScreenType';
import { ProvisionScreenTypeService } from 'src/app/modules/quotation/services/provision.screen.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-provision-screen-type',
  templateUrl: './select-provision-screen-type.component.html',
  styleUrls: ['./select-provision-screen-type.component.css']
})
export class SelectProvisionScreenTypeComponent extends GenericSelectComponent<ProvisionScreenType> implements OnInit {

  types: ProvisionScreenType[] = [] as Array<ProvisionScreenType>;

  constructor(private formBuild: UntypedFormBuilder, private provisionScreenTypeService: ProvisionScreenTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.provisionScreenTypeService.getProvisionScreenTypes().subscribe(response => {
      this.types = response;
    })
  }
}
