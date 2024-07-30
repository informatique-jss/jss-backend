import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ServiceFamily } from 'src/app/modules/quotation/model/ServiceFamily';
import { ServiceFamilyService } from 'src/app/modules/quotation/services/service.family.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-service-family',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectServiceFamilyComponent extends GenericSelectComponent<ServiceFamily> implements OnInit {

  @Input() types: ServiceFamily[] = [] as Array<ServiceFamily>;

  constructor(private formBuild: UntypedFormBuilder,
    private serviceFamilyService: ServiceFamilyService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.serviceFamilyService.getServiceFamilies().subscribe(response => {
      this.types = response;
    })
  }

  displayLabel(object: any): string {
    if (object && object.label && object.code)
      return object.code + ' ' + object.label;
    if (typeof object === "string")
      return object;
    return "";
  }
}
