import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { jarallax } from 'jarallax';
import { APPLICATION_CV_ENTITY_TYPE } from '../../../../libs/Constants';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { SingleUploadComponent } from '../../../miscellaneous/components/forms/single-upload/single-upload.component';
import { AttachmentType } from '../../../my-account/model/AttachmentType';
import { IAttachment } from '../../../my-account/model/IAttachment';
import { AttachmentService } from '../../../my-account/services/attachment.service';
import { Candidacy } from '../../../profile/model/Candidacy';
import { CandidacyService } from '../../services/candidacy.service';

@Component({
  selector: 'join-us',
  templateUrl: './join-us.component.html',
  styleUrls: ['./join-us.component.css'],
  standalone: false
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
  @ViewChild('spontaneousApplicationModal') spontaneousApplicationModal!: ElementRef<HTMLElement>;
  @ViewChild(SingleUploadComponent) singleUploadComponent: SingleUploadComponent | undefined;

  mail: string = "";
  searchedJob: string = "";
  message: string = "";
  APPLICATION_CV_ENTITY_TYPE = APPLICATION_CV_ENTITY_TYPE;
  attachmentTypeApplicationCv: AttachmentType = this.constantService.getAttachmentTypeApplicationCv();
  newCandidacy: Candidacy = {} as Candidacy;
  cvFile: IAttachment = {} as IAttachment;
  modalInstance: any;

  constructor(private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private candidacyService: CandidacyService,
    private appService: AppService,
    private attachmentService: AttachmentService,
  ) { }

  ngOnInit() {
  }

  applicationForm = this.formBuilder.group({});

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.6
    });
  }

  openImageModal(imageUrl: string): void {
    this.applicationForm.reset();
    if (this.modalImageRef) {
      this.modalImageRef.nativeElement.src = imageUrl;
    }
  }

  sendApplication() {
    if (!this.newCandidacy.mail || !this.cvFile || !this.newCandidacy.message) {
      return;
    }
    this.candidacyService.addOrUpdateCandidacy(this.newCandidacy).subscribe(response => {
      if (response) {
        this.newCandidacy = response;
        if (this.singleUploadComponent && this.singleUploadComponent.files && this.singleUploadComponent.files.length > 0) {
          this.singleUploadComponent.entity = response;
          this.singleUploadComponent.uploadFiles();
        }
      }
    });
  }

  refreshForm(last: any) {
    if (last) {
      this.applicationForm.reset();
      this.closeApplicationModal();
      this.appService.displayToast("Nous vous contacterons dans les plus brefs délais.", false, "Candidature envoyée", 3000);
    }
  }

  closeApplicationModal() {
    if (this.modalInstance)
      this.modalInstance.hide();
  }

  openApplicationModal() {
    const modalElement = this.spontaneousApplicationModal.nativeElement;
    this.modalInstance = new (window as any).bootstrap.Modal(modalElement);
    this.modalInstance.show();
  }
}
