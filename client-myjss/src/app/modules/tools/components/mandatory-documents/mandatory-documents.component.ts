import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { jarallax } from 'jarallax';
import { SERVICE_FIELD_TYPE_SELECT } from '../../../../libs/Constants';
import { ServiceFieldType } from '../../../my-account/model/ServiceFieldType';
import { ServiceType } from '../../../my-account/model/ServiceType';
import { TypeDocument } from '../../../my-account/model/TypeDocument';
import { ServiceFieldTypeService } from '../../../my-account/services/service.field.type.service';
import { ServiceFamily } from '../../../quotation/model/ServiceFamily';
import { ServiceFamilyService } from '../../../quotation/services/service.family.service';
import { ServiceTypeService } from '../../../quotation/services/service.type.service';
import { TypeDocumentService } from '../../../quotation/services/type.document.service';

@Component({
  selector: 'mandatory-documents',
  templateUrl: './mandatory-documents.component.html',
  styleUrls: ['./mandatory-documents.component.css'],
  standalone: false
})
export class MandatoryDocumentsComponent implements OnInit {

  debounce: any;
  searchText: string = "";
  serviceFamilies: ServiceFamily[] = [];
  expandedCardIndex: number = -1;
  serviceTypesFullLoaded: number[] = [];
  selectedFamilyTab: ServiceFamily = {} as ServiceFamily;
  serviceTypesByFamily: { [key: number]: Array<ServiceType> } = {};
  filteredServiceTypesByFamily: { [key: number]: Array<ServiceType> } = {};
  mandatoryDocumentsByServiceTypes: { [key: number]: Array<TypeDocument | ServiceFieldType> } = {};

  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;

  constructor(private formBuilder: FormBuilder,
    private serviceFamilyService: ServiceFamilyService,
    private serviceTypeService: ServiceTypeService,
    private typeDocumentService: TypeDocumentService,
    private serviceFieldTypeService: ServiceFieldTypeService
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
              this.serviceTypesByFamily[serviceFamily.id] = response;
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

    if (this.expandedCardIndex >= 0 && serviceType.id && this.serviceTypesFullLoaded.indexOf(serviceType.id) < 0) {
      this.typeDocumentService.getTypeDocumentMandatoryByServiceType(serviceType).subscribe(response => {
        if (response && serviceType.id) {
          if (!this.mandatoryDocumentsByServiceTypes[serviceType.id])
            this.mandatoryDocumentsByServiceTypes[serviceType.id] = [];
          this.mandatoryDocumentsByServiceTypes[serviceType.id] = response;
          this.serviceTypesFullLoaded.push(serviceType.id);
        }
        this.serviceFieldTypeService.getServiceFieldTypesByServiceType(serviceType).subscribe(response => {
          if (response) {
            if (!this.mandatoryDocumentsByServiceTypes[serviceType.id])
              this.mandatoryDocumentsByServiceTypes[serviceType.id] = [];
            if (this.serviceTypesFullLoaded.indexOf(serviceType.id) < 0)
              this.serviceTypesFullLoaded.push(serviceType.id);
          }
        });
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
        this.filteredServiceTypesByFamily[serviceFamily.id] = this.serviceTypesByFamily[serviceFamily.id].
          filter(serviceType => serviceType.customLabel.toLowerCase().includes(this.searchText.toLowerCase()));
  }

  clearSearch() {
    this.searchText = '';
    this.applyFilterOnServiceTypes();
  }
}
