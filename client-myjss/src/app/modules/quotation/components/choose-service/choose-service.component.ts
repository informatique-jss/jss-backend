import { Component, ElementRef, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ChangeEvent } from '@ckeditor/ckeditor5-angular';
import { Alignment, Bold, ClassicEditor, Essentials, Font, GeneralHtmlSupport, Indent, IndentBlock, Italic, Link, List, Mention, Paragraph, PasteFromOffice, RemoveFormat, Underline, Undo } from 'ckeditor5';
import { ConstantService } from '../../../../libs/constant.service';
import { validateSiret } from '../../../../libs/CustomFormsValidatorsHelper';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { Affaire } from '../../../my-account/model/Affaire';
import { ServiceType } from '../../../my-account/model/ServiceType';
import { AffaireService } from '../../../my-account/services/affaire.service';
import { City } from '../../../profile/model/City';
import { Civility } from '../../../profile/model/Civility';
import { Country } from '../../../profile/model/Country';
import { Department } from '../../../profile/model/Department';
import { NoticeType } from '../../model/NoticeType';
import { NoticeTypeFamily } from '../../model/NoticeTypeFamily';
import { ServiceFamily } from '../../model/ServiceFamily';
import { ServiceFamilyGroup } from '../../model/ServiceFamilyGroup';
import { ServiceTypeChosen } from '../../model/ServiceTypeChosen';
import { CityService } from '../../services/city.service';
import { CivilityService } from '../../services/civility.service';
import { CountryService } from '../../services/country.service';
import { DepartmentService } from '../../services/department.service';
import { NoticeTypeFamilyService } from '../../services/notice.type.family.service';
import { NoticeTypeService } from '../../services/notice.type.service';
import { ServiceFamilyGroupService } from '../../services/service.family.group.service';
import { ServiceFamilyService } from '../../services/service.family.service';
import { ServiceTypeService } from '../../services/service.type.service';

@Component({
  selector: 'choose-service',
  templateUrl: './choose-service.component.html',
  styleUrls: ['./choose-service.component.css']
})
export class ChooseServiceComponent implements OnInit {

  @Output() onChoseService = new EventEmitter<ServiceTypeChosen>();
  @Input() currentOrderAffaires: Affaire[] | undefined;
  @Input() editedService: ServiceTypeChosen | undefined;
  service: ServiceType = {} as ServiceType;
  currentStep: number = 1;
  serviceFamilyGroupChosen: ServiceFamilyGroup | undefined;
  serviceFamilyChosen: ServiceFamily | undefined;
  serviceTypeChosen: ServiceType | undefined;

  serviceFamilyGroups: ServiceFamilyGroup[] | undefined;
  serviceFamilies: ServiceFamily[] | undefined;
  serviceTypes: ServiceType[] | undefined;
  filteredServiceTypes: ServiceType[] | undefined;

  searchService: string = "";
  //  siretSearched: string = "48339332800024"; //debug
  siretSearched: string = ""; //debug
  affaire: Affaire = { isIndividual: false } as Affaire;
  loadingAffaireSearch: boolean = false;
  checkedOnce = false;

  announcementPublicationDate: Date = new Date();
  announcementRedactedByJss: Boolean = true;
  announcementProofReading: Boolean | undefined;
  announcementNoticeFamily: NoticeTypeFamily | undefined;
  announcementNoticeType: NoticeType | undefined;
  announcementDepartment: Department | undefined;
  announcementNotice: string | undefined;
  announcementNoticeInit: string = "";

  noticeTypes: NoticeType[] | undefined;
  noticeTypeFamilies: NoticeTypeFamily[] | undefined;
  departments: Department[] | undefined;
  civilities: Civility[] | undefined;

  minDatePublication: Date = new Date();

  @ViewChild('step2') step2: ElementRef | undefined;
  @ViewChild('step3') step3: ElementRef | undefined;
  @ViewChild('step4') step4: ElementRef | undefined;
  @ViewChild('step5') step5: ElementRef | undefined;
  @ViewChild('genericInput') genericInput: ElementRef | undefined;

  countryFrance: Country = this.constantService.getCountryFrance();
  countries: Country[] | undefined;
  foundCities: City[] | undefined;

  debounce: any;
  temporaryId: number | undefined;

  ckEditorHeader = ClassicEditor;
  initialNoticeValue: string = "";

  constructor(
    private serviceFamilyGroupService: ServiceFamilyGroupService,
    private serviceFamilyService: ServiceFamilyService,
    private serviceTypeService: ServiceTypeService,
    private formBuilder: FormBuilder,
    private affaireService: AffaireService,
    private constantService: ConstantService,
    private countryService: CountryService,
    private cityService: CityService,
    private noticeTypeFamilyService: NoticeTypeFamilyService,
    private noticeTypeService: NoticeTypeService,
    private departmentService: DepartmentService,
    private civilityService: CivilityService,
  ) { }

  addServiceForm = this.formBuilder.group({});
  capitalizeName = capitalizeName;

  ngOnInit() {
    if (!this.serviceFamilyGroups)
      this.serviceFamilyGroupService.getServiceFamilyGroups().subscribe(response => {
        this.serviceFamilyGroups = response;
        //this.choseFamilyGroup(response[2]); //debug
      })

    this.countryService.getCountries().subscribe(response => {
      this.countries = response.sort((a: Country, b: Country) => a.label.localeCompare(b.label));
      for (let country of this.countries)
        if (country.id == this.countryFrance.id)
          this.affaire.country = country;
      //this.affaireService.getAffaire(4364468).subscribe(affaire => { this.affaire = affaire }); //debug
      // this.validateAffaire(); //debug
    })

    if (this.editedService) {
      this.affaire = this.editedService.affaire;
      this.serviceTypeChosen = this.editedService.service;
      this.announcementPublicationDate = this.editedService.announcementPublicationDate;
      this.announcementRedactedByJss = this.editedService.announcementRedactedByJss;
      this.announcementProofReading = this.editedService.announcementProofReading;
      this.announcementNoticeFamily = this.editedService.announcementNoticeFamily;
      this.announcementNoticeType = this.editedService.announcementNoticeType;
      this.announcementDepartment = this.editedService.announcementDepartment;
      this.announcementNotice = this.editedService.announcementNotice;
      if (this.announcementNotice)
        this.announcementNoticeInit = this.announcementNotice;
      this.serviceFamilyGroupChosen = this.editedService.serviceFamilyGroupChosen;
      this.serviceFamilyChosen = this.editedService.serviceFamilyChosen;
      this.temporaryId = this.editedService.temporaryId;
      if (!this.serviceTypeChosen.hasAnnouncement && (this.serviceTypeChosen.isRequiringNewUnregisteredAffaire || this.affaire && this.affaire.id))
        this.currentStep = 4;
      else {
        this.currentStep = 5;
        this.fetchAnnouncementReferentials();
      }
    }
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  validateService() {
    if (this.serviceTypeChosen) {
      if (!this.affaire.id) {
        this.checkedOnce = true;
        if (this.affaire.isIndividual) {
          if (!this.affaire.firstname || !this.affaire.lastname)
            return;
        } else if (!this.affaire.denomination)
          return;
        if (!this.affaire.city || !this.affaire.country)
          return;
        if (this.affaire.country.id == this.countryFrance.id && (!this.affaire.postalCode || this.affaire.postalCode.length < 4))
          return;
      }
      if (this.serviceTypeChosen.hasAnnouncement && !this.announcementRedactedByJss) {
        if (!this.announcementDepartment || !this.announcementNotice || !this.announcementNoticeType)
          return;
      }
    }
    this.onChoseService.emit({
      affaire: this.affaire,
      service: this.serviceTypeChosen,
      announcementPublicationDate: this.announcementPublicationDate,
      announcementRedactedByJss: this.announcementRedactedByJss,
      announcementProofReading: this.announcementProofReading,
      announcementNoticeFamily: this.announcementNoticeFamily,
      announcementNoticeType: this.announcementNoticeType,
      announcementDepartment: this.announcementDepartment,
      announcementNotice: this.announcementNotice,
      serviceFamilyGroupChosen: this.serviceFamilyGroupChosen,
      serviceFamilyChosen: this.serviceFamilyChosen,
      temporaryId: this.temporaryId
    } as ServiceTypeChosen);
  }

  searchServices() {
    if (this.serviceTypes)
      this.filteredServiceTypes = this.serviceTypes.filter(service => JSON.stringify(service).toLowerCase().indexOf(this.searchService) >= 0);
  }

  choseFamilyGroup(serviceFamilyGroup: ServiceFamilyGroup) {
    this.serviceFamilyGroupChosen = serviceFamilyGroup;
    this.serviceFamilies = undefined;
    if (this.serviceFamilyGroupChosen) {
      this.currentStep++;
      this.serviceFamilyService.getServiceFamiliesForFamilyGroup(this.serviceFamilyGroupChosen.id).subscribe(response => {
        this.serviceFamilies = response;
        if (this.step2)
          setTimeout(this.step2.nativeElement.scrollIntoView({ behavior: 'smooth' }), 0);
        //  this.choseFamily(this.serviceFamilies[1]); //debug
      })
    }
  }

  choseFamily(serviceFamily: ServiceFamily) {
    this.serviceFamilyChosen = serviceFamily;
    this.serviceTypes = undefined;
    if (this.serviceFamilyChosen) {
      this.currentStep++;
      this.serviceTypeService.getServiceTypesForFamily(this.serviceFamilyChosen.id).subscribe(response => {
        this.serviceTypes = response;
        this.searchServices();
        if (this.step3)
          setTimeout(this.step3.nativeElement.scrollIntoView({ behavior: 'smooth' }), 200);
        if (this.genericInput)
          setTimeout(this.genericInput.nativeElement.focus(), 200);
        //this.choseServiceType(this.serviceTypes[1]); //debug
      })
    }
  }

  choseServiceType(serviceType: ServiceType) {
    this.serviceTypeChosen = serviceType;
    if (this.serviceTypeChosen) {
      this.service = this.serviceTypeChosen;
      if (this.service.hasOnlyAnnouncement)
        this.announcementRedactedByJss = true;
      if (this.service.hasAnnouncement) {
        this.fetchAnnouncementReferentials();
        this.validateService();
      }

      this.currentStep++;
      if (this.step4)
        setTimeout(this.step4.nativeElement.scrollIntoView({ behavior: 'smooth', block: "end" }), 200);
    }
  }

  fetchAnnouncementReferentials() {
    if (!this.civilities)
      this.civilityService.getCivilities().subscribe(reponse => this.civilities = reponse);
    if (!this.noticeTypeFamilies)
      this.noticeTypeFamilyService.getNoticeTypeFamilies().subscribe(response => this.noticeTypeFamilies = response.sort((a: NoticeTypeFamily, b: NoticeTypeFamily) => a.label.localeCompare(b.label)));
    if (!this.noticeTypes)
      this.noticeTypeService.getNoticeTypes().subscribe(response => this.noticeTypes = response.sort((a: NoticeType, b: NoticeType) => a.label.localeCompare(b.label)));
    if (!this.departments)
      this.departmentService.getDepartments().subscribe(response => {
        this.departments = response.sort((a: Department, b: Department) => a.code.localeCompare(b.code));
        if (this.departments && this.affaire && this.affaire.city && this.affaire.city.department)
          for (let department of this.departments)
            if (department.id == this.affaire.city.department.id) {
              this.announcementDepartment = department;
            }
      });
  }


  searchSiret() {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.effectiveSearchSiret();
    }, 500);
  }

  effectiveSearchSiret() {
    if (this.siretSearched && validateSiret(this.siretSearched)) {
      this.loadingAffaireSearch = true;
      this.affaireService.getAffaireBySiret(this.siretSearched).subscribe(response => {
        this.loadingAffaireSearch = false;
        if (response && response.length == 1)
          this.affaire = response[0];
        if (this.affaire && this.affaire.country && this.countries)
          for (let country of this.countries)
            if (country.id == this.affaire.country.id) {
              this.affaire.country = country;
              break;
            }
      })
    }
  }

  fetchCities() {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.effectiveFetchCities();
    }, 500);
  }

  effectiveFetchCities() {
    this.foundCities = undefined;
    if (this.affaire)
      if (this.affaire.country && this.affaire.country.id) {
        if (this.affaire.country.id != this.countryFrance.id) {
          this.cityService.getCitiesByCountry(this.affaire.country).subscribe(response => {
            this.foundCities = response;
          })
        } else if (this.affaire.postalCode && this.affaire.postalCode.length > 4) {
          this.cityService.getCitiesFilteredByCountryAndNameAndPostalCode(this.affaire.country, this.affaire.postalCode).subscribe(response => {
            this.foundCities = response;
            if (this.foundCities.length == 1)
              this.affaire.city = this.foundCities[0];
          })
        }
      }
  }

  validateAffaire() {
    this.currentStep++;
    if (this.step5)
      setTimeout(this.step5.nativeElement.scrollIntoView({ behavior: 'smooth' }), 200);
  }

  config = {
    toolbar: ['undo', 'redo', '|', 'fontFamily', 'fontSize', 'bold', 'italic', 'underline', 'fontColor', 'fontBackgroundColor', '|',
      'alignment:left', 'alignment:right', 'alignment:center', 'alignment:justify', '|', 'link', 'bulletedList', 'numberedList', 'outdent', 'indent', 'removeformat'
    ],
    plugins: [
      Bold, Essentials, Italic, Mention, Paragraph, Undo, Font, Alignment, Link, List, Indent, IndentBlock, RemoveFormat, GeneralHtmlSupport, Underline, PasteFromOffice],
    htmlSupport: {
      allow: [
        {
          name: /.*/,
          attributes: true,
          classes: true,
          styles: true
        }
      ]
    }
  } as any;

  onNoticeChange(event: ChangeEvent) {
    this.announcementNotice = event.editor.getData();
  }

}
