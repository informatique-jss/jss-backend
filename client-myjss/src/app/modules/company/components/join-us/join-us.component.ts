import { Component, ElementRef, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { APPLICATION_CV_ENTITY_TYPE } from '../../../../libs/Constants';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Mail } from '../../../general/model/Mail';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { PlatformService } from '../../../main/services/platform.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../../../miscellaneous/components/forms/generic-textarea/generic-textarea.component';
import { SingleUploadComponent } from '../../../miscellaneous/components/forms/single-upload/single-upload.component';
import { GenericSwiperComponent } from '../../../miscellaneous/components/generic-swiper/generic-swiper.component';
import { AttachmentType } from '../../../my-account/model/AttachmentType';
import { Candidacy } from '../../../profile/model/Candidacy';
import { CandidacyService } from '../../services/candidacy.service';

@Component({
  selector: 'join-us',
  templateUrl: './join-us.component.html',
  styleUrls: ['./join-us.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent, GenericTextareaComponent, SingleUploadComponent, GenericSwiperComponent]
})
export class JoinUsComponent implements OnInit {

  images: string[] = [
    "assets/img/societe/join_us_caroussel1.png",
    "assets/img/societe/join_us_caroussel2.png",
    "assets/img/societe/join_us_caroussel3.png",
    "assets/img/societe/join_us_caroussel1.png",
    "assets/img/societe/join_us_caroussel2.png",
    "assets/img/societe/join_us_caroussel3.png",
  ];

  @ViewChild('modalImage') modalImageRef!: ElementRef<HTMLImageElement>;
  @ViewChild('imageModal') imageModal!: ElementRef<HTMLImageElement>;
  @ViewChild('spontaneousApplicationModal') spontaneousApplicationModal!: TemplateRef<any>;
  @ViewChild(SingleUploadComponent) singleUploadComponent: SingleUploadComponent | undefined;

  APPLICATION_CV_ENTITY_TYPE = APPLICATION_CV_ENTITY_TYPE;
  attachmentTypeApplicationCv!: AttachmentType;
  newCandidacy: Candidacy = { mail: {} as Mail } as Candidacy;
  modalInstance: any;
  applicationForm!: FormGroup;
  applicationlInstance: any | undefined;
  currentImageLink: string = "";

  constructor(private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private candidacyService: CandidacyService,
    private appService: AppService,
    private platformService: PlatformService,
    private modalService: NgbModal,
  ) { }

  ngOnInit() {
    this.attachmentTypeApplicationCv = this.constantService.getAttachmentTypeApplicationCv();
    this.applicationForm = this.formBuilder.group({});
  }

  ngAfterViewInit(): void {
    if (this.platformService.getNativeDocument())
      import('jarallax').then(module => {
        module.jarallax(this.platformService.getNativeDocument()!.querySelectorAll('.jarallax'), {
          speed: 0.6
        });
      });

  }

  openImageModal(imageUrl: string): void {
    this.currentImageLink = imageUrl;
    if (this.applicationlInstance) {
      return;
    }

    this.applicationlInstance = this.modalService.open(this.imageModal, {
      fullscreen: true
    });

    this.applicationlInstance.result.finally(() => {
      this.applicationlInstance = undefined;
    });
  }

  sendApplication() {
    if (!this.newCandidacy.mail || !this.newCandidacy.message) {
      return;
    }
    this.candidacyService.addOrUpdateCandidacy(this.newCandidacy).subscribe(response => {
      if (response) {
        if (this.singleUploadComponent && this.singleUploadComponent.files && this.singleUploadComponent.files.length > 0) {
          this.singleUploadComponent.entity = response;
          this.singleUploadComponent.uploadFiles();
        } else {
          this.refreshForm(true);
        }
      }
    });
  }

  refreshForm(last: any) {
    if (last) {
      this.newCandidacy = { mail: {} as Mail } as Candidacy;
      this.applicationForm.reset();
      if (this.singleUploadComponent)
        this.singleUploadComponent.files = [];
      this.closeApplicationModal();
      this.appService.displayToast("Nous vous contacterons dans les plus brefs délais.", false, "Candidature envoyée", 3000);
    }
  }

  closeApplicationModal() {
    if (this.applicationlInstance)
      this.applicationlInstance.close();
  }

  openApplicationModal() {
    if (this.applicationlInstance) {
      return;
    }

    this.applicationlInstance = this.modalService.open(this.spontaneousApplicationModal, {
      size: "lg"
    });

    this.applicationlInstance.result.finally(() => {
      this.applicationlInstance = undefined;
    });
  }
}
