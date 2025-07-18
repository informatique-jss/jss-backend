import { Component, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ServiceType } from 'src/app/modules/quotation/model/ServiceType';
import { ServiceTypeService } from 'src/app/modules/quotation/services/service.type.service';
import { AppService } from 'src/app/services/app.service';
import { ConstantService } from '../../../services/constant.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-service-type',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteServiceTypeComponent extends GenericLocalAutocompleteComponent<ServiceType> implements OnInit {

  types: ServiceType[] = [] as Array<ServiceType>;

  constructor(private formBuild: UntypedFormBuilder, private serviceTypeService: ServiceTypeService,
    private constantService: ConstantService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
  }

  filterEntities(types: ServiceType[], value: string): ServiceType[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(serviceType =>
      serviceType.label != undefined && (serviceType.label.toLowerCase().includes(filterValue)) || serviceType.code != undefined && (serviceType.code.toLowerCase().includes(filterValue)));
  }

  initTypes(): void {
    this.serviceTypeService.getServiceTypesComplete().subscribe(response => {
      this.types = response;
    });
  }

  displayLabel(object: ServiceType): string {
    let label = "";
    if (object) {
      if (object.serviceFamily && object.serviceFamily.serviceFamilyGroup)
        label += object.serviceFamily.serviceFamilyGroup.label + " - ";
      label += object.label;
    }
    return label;
  }
}
