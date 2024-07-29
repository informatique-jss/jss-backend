import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BuildingDomiciliation } from 'src/app/modules/quotation/model/BuildingDomiciliation';
import { BuildingDomiciliationService } from 'src/app/modules/quotation/services/building.domiciliation.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';


@Component({
  selector: 'select-building-domiciliation',
  templateUrl: './select-building-domicilation.component.html',
  styleUrls: ['./select-building-domicilation.component.css']
})
export class SelectBuildingDomicilationComponent extends GenericSelectComponent<BuildingDomiciliation> implements OnInit {

  types: BuildingDomiciliation[] = [] as Array<BuildingDomiciliation>;

  constructor(private formBuild: UntypedFormBuilder, private buildingDomiciliationService: BuildingDomiciliationService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.buildingDomiciliationService.getBuildingDomiciliations().subscribe(response => {
      this.types = response;
    })
  }
}
