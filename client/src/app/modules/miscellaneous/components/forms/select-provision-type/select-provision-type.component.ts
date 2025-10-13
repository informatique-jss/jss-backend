import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ProvisionType } from 'src/app/modules/quotation/model/ProvisionType';
import { ProvisionTypeService } from 'src/app/modules/quotation/services/provision.type.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { ProvisionFamilyType } from '../../../../quotation/model/ProvisionFamilyType';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-provision-type',
  templateUrl: './select-provision-type.component.html',
  styleUrls: ['./select-provision-type.component.css']
})
export class SelectProvisionTypeComponent extends GenericSelectComponent<ProvisionType> implements OnInit {

  types: ProvisionType[] = [] as Array<ProvisionType>;

  @Input() filteredProvisionFamilyType: ProvisionFamilyType | undefined;

  constructor(private formBuild: UntypedFormBuilder, private provisionTypeService: ProvisionTypeService, private appService3: AppService,
    private habiltiationService: HabilitationsService
  ) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.provisionTypeService.getProvisionTypes().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label));
      if (!this.habiltiationService.isAdministrator())
        this.types = this.types.filter(t => !t.isCanBeAddByAdmin);
    })
  }
}
