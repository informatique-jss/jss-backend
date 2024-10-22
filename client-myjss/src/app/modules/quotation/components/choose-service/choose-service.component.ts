import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ServiceType } from '../../../my-account/model/ServiceType';
import { ServiceFamily } from '../../model/ServiceFamily';
import { ServiceFamilyGroup } from '../../model/ServiceFamilyGroup';
import { ServiceFamilyGroupService } from '../../services/service.family.group.service';
import { ServiceFamilyService } from '../../services/service.family.service';
import { ServiceTypeService } from '../../services/service.type.service';

@Component({
  selector: 'choose-service',
  templateUrl: './choose-service.component.html',
  styleUrls: ['./choose-service.component.css']
})
export class ChooseServiceComponent implements OnInit {

  @Output() onChoseService = new EventEmitter<ServiceType>();
  service: ServiceType = {} as ServiceType;
  currentStep: number = 1;
  serviceFamilyGroupChosen: ServiceFamilyGroup | undefined;
  serviceFamilyChosen: ServiceFamily | undefined;
  serviceTypeChosen: ServiceType | undefined;

  serviceFamilyGroups: ServiceFamilyGroup[] | undefined;
  serviceFamilies: ServiceFamily[] | undefined;
  serviceTypes: ServiceType[] | undefined;

  searchService: string = "";

  constructor(
    private serviceFamilyGroupService: ServiceFamilyGroupService,
    private serviceFamilyService: ServiceFamilyService,
    private serviceTypeService: ServiceTypeService,
    private formBuilder: FormBuilder
  ) { }

  addServiceForm = this.formBuilder.group({});

  ngOnInit() {
    this.serviceFamilyGroupService.getServiceFamilyGroups().subscribe(response => {
      this.serviceFamilyGroups = response;
    })
  }

  validateService() {
    this.onChoseService.emit(this.service);
  }

  choseFamilyGroup(serviceFamilyGroup: ServiceFamilyGroup) {
    this.serviceFamilyGroupChosen = serviceFamilyGroup;
    if (this.serviceFamilyGroupChosen)
      this.serviceFamilyService.getServiceFamiliesForFamilyGroup(this.serviceFamilyGroupChosen.id).subscribe(response => {
        this.serviceFamilies = response;
        this.currentStep++;
      })
  }

  choseFamily(serviceFamily: ServiceFamily) {
    this.serviceFamilyChosen = serviceFamily;
    if (this.serviceFamilyChosen)
      this.serviceTypeService.getServiceTypesForFamily(this.serviceFamilyChosen.id).subscribe(response => {
        this.serviceTypes = response;
        this.currentStep++;
      })
  }

  choseServiceType(serviceType: ServiceType) {
    this.serviceTypeChosen = serviceType;
    if (this.serviceTypeChosen) {
      this.service = this.serviceTypeChosen;
      this.validateService();
    }
  }

}
