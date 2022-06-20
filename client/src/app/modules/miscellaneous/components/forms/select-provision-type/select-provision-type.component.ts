import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ProvisionType } from 'src/app/modules/quotation/model/ProvisionType';
import { ProvisionTypeService } from 'src/app/modules/quotation/services/provision.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-provision-type',
  templateUrl: './select-provision-type.component.html',
  styleUrls: ['./select-provision-type.component.css']
})
export class SelectProvisionTypeComponent extends GenericSelectComponent<ProvisionType> implements OnInit {

  types: ProvisionType[] = [] as Array<ProvisionType>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: FormBuilder, private provisionTypeService: ProvisionTypeService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.provisionTypeService.getProvisionTypes().subscribe(response => {
      this.types = response;
    })
  }
}
