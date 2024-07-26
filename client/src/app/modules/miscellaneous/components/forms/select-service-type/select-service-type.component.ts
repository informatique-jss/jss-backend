import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ServiceType } from 'src/app/modules/quotation/model/ServiceType';
import { ServiceTypeService } from 'src/app/modules/quotation/services/service.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-service-type',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectServiceTypeComponent extends GenericSelectComponent<ServiceType> implements OnInit {

  @Input() types: ServiceType[] = [] as Array<ServiceType>;

  constructor(private formBuild: UntypedFormBuilder,
    private serviceTypeService: ServiceTypeService,) {
    super(formBuild)
  }

  initTypes(): void {
    this.serviceTypeService.getServiceTypes().subscribe(response => {
      this.types = response;
    })
  }
}
