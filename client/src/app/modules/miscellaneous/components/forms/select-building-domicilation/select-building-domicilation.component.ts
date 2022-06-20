import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { BuildingDomiciliation } from 'src/app/modules/quotation/model/BuildingDomiciliation';
import { BuildingDomiciliationService } from 'src/app/modules/quotation/services/building.domiciliation.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';


@Component({
  selector: 'select-building-domiciliation',
  templateUrl: './select-building-domicilation.component.html',
  styleUrls: ['./select-building-domicilation.component.css']
})
export class SelectBuildingDomicilationComponent extends GenericSelectComponent<BuildingDomiciliation> implements OnInit {

  types: BuildingDomiciliation[] = [] as Array<BuildingDomiciliation>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: FormBuilder, private BuildingDomiciliationService: BuildingDomiciliationService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.BuildingDomiciliationService.getBuildingDomiciliations().subscribe(response => {
      this.types = response;
    })
  }
}
