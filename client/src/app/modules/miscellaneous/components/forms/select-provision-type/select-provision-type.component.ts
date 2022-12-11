import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ProvisionType } from 'src/app/modules/quotation/model/ProvisionType';
import { ProvisionTypeService } from 'src/app/modules/quotation/services/provision.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
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

  constructor(private formBuild: UntypedFormBuilder, private provisionTypeService: ProvisionTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.provisionTypeService.getProvisionTypes().subscribe(response => {
      console.log(response);
      this.types = response;
    })
  }
}
