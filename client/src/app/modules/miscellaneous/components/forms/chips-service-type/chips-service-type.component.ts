import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { ServiceType } from 'src/app/modules/quotation/model/ServiceType';
import { ServiceTypeService } from 'src/app/modules/quotation/services/service.type.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { ConstantService } from '../../../services/constant.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-service-type',
  templateUrl: './chips-service-type.component.html',
  styleUrls: ['./chips-service-type.component.css']
})
export class ChipsServiceTypeComponent extends GenericChipsComponent<ServiceType> implements OnInit {

  serviceTypes: ServiceType[] = [] as Array<ServiceType>;
  filteredServiceTypes: Observable<ServiceType[]> | undefined;
  @Input() isDisplayOnlyServiceForUnregisteredAffaire: boolean = false;
  @ViewChild('serviceTypeInput') serviceTypeInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder, private habilitationService: HabilitationsService,
    private serviceTypeService: ServiceTypeService, private appService3: AppService, private constantService: ConstantService) {
    super(formBuild, appService3)
  }

  callOnNgInit(): void {
    this.serviceTypeService.getServiceTypes().subscribe(response => {
      if (this.isDisplayOnlyServiceForUnregisteredAffaire)
        this.serviceTypes = response.filter(service => service.isRequiringNewUnregisteredAffaire);
      else
        this.serviceTypes = response;
    })
    if (this.form)
      this.filteredServiceTypes = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this._filterByName(this.serviceTypes, value))
      );
  }

  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: ServiceType): ServiceType {
    return object;
  }

  getValueFromObject(object: ServiceType): string {
    return object.label;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.label != undefined
      && input.label.toLowerCase().includes(filterValue)
      && (input.id != this.constantService.getServiceTypeOther().id || this.habilitationService.canAddServiceTypeOther()));
  }

  addServiceType(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<ServiceType>;
      // Do not add twice
      if (this.model.map(serviceType => serviceType.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.serviceTypeInput!.nativeElement.value = '';
    }
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
