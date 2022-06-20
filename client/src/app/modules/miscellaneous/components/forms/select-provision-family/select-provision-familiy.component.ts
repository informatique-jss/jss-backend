import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ProvisionFamilyType } from 'src/app/modules/quotation/model/ProvisionFamilyType';
import { ProvisionFamilyTypeService } from 'src/app/modules/quotation/services/provision.family.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-provision-family',
  templateUrl: './select-provision-family.component.html',
  styleUrls: ['./select-provision-family.component.css']
})
export class SelectProvisionFamilyComponent extends GenericSelectComponent<ProvisionFamilyType> implements OnInit {

  types: ProvisionFamilyType[] = [] as Array<ProvisionFamilyType>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder, private provisionFamilyTypeService: ProvisionFamilyTypeService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.provisionFamilyTypeService.getProvisionFamilyTypes().subscribe(response => {
      this.types = response;
    })
  }
}
