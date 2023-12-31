import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ProvisionFamilyType } from 'src/app/modules/quotation/model/ProvisionFamilyType';
import { ProvisionFamilyTypeService } from 'src/app/modules/quotation/services/provision.family.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-provision-family',
  templateUrl: './select-provision-family.component.html',
  styleUrls: ['./select-provision-family.component.css']
})
export class SelectProvisionFamilyComponent extends GenericSelectComponent<ProvisionFamilyType> implements OnInit {

  types: ProvisionFamilyType[] = [] as Array<ProvisionFamilyType>;

  constructor(private formBuild: UntypedFormBuilder, private provisionFamilyTypeService: ProvisionFamilyTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.provisionFamilyTypeService.getProvisionFamilyTypes().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label));
    })
  }
}
