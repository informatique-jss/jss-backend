import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ProvisionFamilyType } from 'src/app/modules/quotation/model/ProvisionFamilyType';
import { ProvisionFamilyTypeService } from 'src/app/modules/quotation/services/provision.family.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-provision-family',
  templateUrl: './select-provision-family.component.html',
  styleUrls: ['./select-provision-family.component.css']
})
export class SelectProvisionFamilyComponent extends GenericSelectComponent<ProvisionFamilyType> implements OnInit {

  types: ProvisionFamilyType[] = [] as Array<ProvisionFamilyType>;

  constructor(private formBuild: UntypedFormBuilder, private provisionFamilyTypeService: ProvisionFamilyTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.provisionFamilyTypeService.getProvisionFamilyTypes().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label));
    })
  }
}
