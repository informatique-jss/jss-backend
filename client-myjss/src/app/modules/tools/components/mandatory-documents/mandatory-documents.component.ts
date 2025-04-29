import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { jarallax } from 'jarallax';
import { SERVICE_FIELD_TYPE_SELECT } from '../../../../libs/Constants';
import { ServiceFieldType } from '../../../my-account/model/ServiceFieldType';
import { ServiceType } from '../../../my-account/model/ServiceType';
import { ServiceFamily } from '../../../quotation/model/ServiceFamily';
import { ServiceFamilyService } from '../../../quotation/services/service.family.service';
import { ServiceTypeService } from '../../../quotation/services/service.type.service';

@Component({
  selector: 'mandatory-documents',
  templateUrl: './mandatory-documents.component.html',
  styleUrls: ['./mandatory-documents.component.css'],
  standalone: false
})
export class MandatoryDocumentsComponent implements OnInit {

  searchText: string = "";
  serviceFamilies: ServiceFamily[] = [];
  expandedCardIndex: number = -1;
  selectedFamilyTab: ServiceFamily = {} as ServiceFamily;
  serviceTypesByFamily: { [key: number]: Array<ServiceType> } = {};
  filteredServiceTypesByFamily: { [key: number]: Array<ServiceType> } = {};

  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;

  constructor(private formBuilder: FormBuilder,
    private serviceFamilyService: ServiceFamilyService,
    private serviceTypeService: ServiceTypeService,
  ) { }

  ngOnInit() {
    this.serviceFamilyService.getServiceFamiliesExcludingFamilyGroupAnnouncement().subscribe(response => {
      if (response) {
        this.serviceFamilies = response;
        this.selectedFamilyTab = this.serviceFamilies[0];
        for (let serviceFamily of this.serviceFamilies) {
          this.serviceTypeService.getServiceTypesForFamily(serviceFamily.id).subscribe(response => {
            if (response) {
              if (!this.serviceTypesByFamily[serviceFamily.id])
                this.serviceTypesByFamily[serviceFamily.id] = [];
              if (response)
                for (let service of response) {
                  this.serviceTypesByFamily[serviceFamily.id][service.id] = service;
                }
              this.applyFilterOnServiceTypes();
            }
          });
        }
      }
    });
  }

  practicalSheetsForm = this.formBuilder.group({});

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

  selectFamilyTab(serviceFamily: ServiceFamily) {
    this.selectedFamilyTab = serviceFamily;
  }

  isExpanded(index: number): boolean {
    return this.expandedCardIndex === index;
  }

  toggleCard(serviceType: ServiceType): void {
    if (this.expandedCardIndex === serviceType.id)
      this.expandedCardIndex = -1;
    else if (serviceType.id)
      this.expandedCardIndex = serviceType.id;

    if (this.expandedCardIndex >= 0 && serviceType.id && (!this.serviceTypesByFamily[this.selectedFamilyTab.id][serviceType.id].assoServiceTypeFieldTypes && !this.serviceTypesByFamily[this.selectedFamilyTab.id][serviceType.id].assoServiceTypeDocuments)) {
      this.serviceTypeService.getServiceTypeWithIsMandatoryDocuments(serviceType, true).subscribe(response => {
        if (response && serviceType.id) {
          this.serviceTypesByFamily[this.selectedFamilyTab.id][serviceType.id] = response;
          this.applyFilterOnServiceTypes();
        }
      });
    }
  }

  getPossibleFieldsValuesForSelect(field: ServiceFieldType): string {
    if (field.dataType == SERVICE_FIELD_TYPE_SELECT) {
      return field.label.concat(' (', field.serviceFieldTypePossibleValues.map(item => item.value).join('/'), ')');
    }
    return "";
  }

  applyFilterOnServiceTypes() {
    if (this.serviceFamilies)
      for (let serviceFamily of this.serviceFamilies)
        if (this.searchText && this.searchText.length > 2)
          this.filteredServiceTypesByFamily[serviceFamily.id] = this.serviceTypesByFamily[serviceFamily.id].
            filter(serviceType => serviceType.customLabel.toLowerCase().includes(this.searchText.toLowerCase()));
        else this.filteredServiceTypesByFamily[serviceFamily.id] = this.serviceTypesByFamily[serviceFamily.id];
  }

  clearSearch() {
    this.searchText = '';
    this.applyFilterOnServiceTypes();
  }
}
