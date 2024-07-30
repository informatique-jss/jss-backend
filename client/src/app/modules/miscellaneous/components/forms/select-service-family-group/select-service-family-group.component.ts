import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ServiceFamilyGroup } from 'src/app/modules/quotation/model/ServiceFamilyGroup';
import { ServiceFamilyGroupService } from '../../../../quotation/services/service.family.group.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-service-family-group',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectServiceFamilyGroupComponent extends GenericSelectComponent<ServiceFamilyGroup> implements OnInit {

  @Input() types: ServiceFamilyGroup[] = [] as Array<ServiceFamilyGroup>;

  constructor(private formBuild: UntypedFormBuilder,
    private serviceFamilyGroupService: ServiceFamilyGroupService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.serviceFamilyGroupService.getServiceFamilyGroups().subscribe(response => {
      this.types = response;
    })
  }
}
