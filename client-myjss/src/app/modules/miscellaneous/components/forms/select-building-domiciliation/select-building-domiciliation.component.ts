import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { BuildingDomiciliation } from '../../../../quotation/model/BuildingDomiciliation';
import { BuildingDomiciliationService } from '../../../../quotation/services/building.domiciliation.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-building-domiciliation',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectBuildingDomiciliationComponent extends GenericSelectComponent<BuildingDomiciliation> implements OnInit {

  @Input() types: BuildingDomiciliation[] = [] as Array<BuildingDomiciliation>;

  constructor(private formBuild: UntypedFormBuilder,
    private buildingDomiciliationService: BuildingDomiciliationService,
  ) {
    super(formBuild)
  }

  initTypes(): void {
    this.buildingDomiciliationService.getBuildingDomiciliations().subscribe(response => {
      this.types = response;
    })
  }
}
