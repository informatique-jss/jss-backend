import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { BillingType } from 'src/app/modules/miscellaneous/model/BillingType';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { UploadAttachmentService } from 'src/app/modules/miscellaneous/services/upload.attachment.service';
import { PROVISION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { Provision } from '../../model/Provision';

@Component({
  selector: 'provision-options',
  templateUrl: './provision-options.component.html',
  styleUrls: ['./provision-options.component.css']
})
export class ProvisionOptionsComponent implements OnInit {

  @Input() provision: Provision | undefined;
  @Input() editMode: boolean = false;

  @Output() provisionChange: EventEmitter<void> = new EventEmitter<void>();

  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;

  billingTypeLogo = this.constantService.getBillingTypeLogo();

  attachmentTypeLogo = this.constantService.getAttachmentTypeLogo();

  logoUrl: SafeUrl | undefined;

  constructor(private formBuilder: FormBuilder,
    private uploadAttachmentService: UploadAttachmentService,
    private constantService: ConstantService,
    private sanitizer: DomSanitizer,
  ) { }

  optionForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (!this.provision!.isLogo) {
      this.provision!.isLogo = false;
    } else {
      this.setLogoUrl();
    }
  }

  displayOption(billingType: BillingType): boolean {
    if (this.provision && billingType && this.provision.provisionType && this.provision.provisionType.billingTypes)
      for (let billingType of this.provision.provisionType.billingTypes)
        if (billingType.id == billingType.id)
          return true;
    return false;
  }


  updateAttachments(attachments: Attachment[]) {
    if (attachments && this.provision) {
      this.provision.attachments = attachments;
      this.setLogoUrl();
    }
  }

  setLogoUrl() {
    if (this.provision && this.provision.attachments != null && this.provision.attachments) {
      this.provision.attachments.forEach(attachment => {
        if (attachment.attachmentType.id == this.attachmentTypeLogo.id)
          this.uploadAttachmentService.previewAttachmentUrl(attachment).subscribe((response: any) => {
            let binaryData = [];
            binaryData.push(response.body);
            let url = window.URL.createObjectURL(new Blob(binaryData, { type: response.headers.get("content-type") }));
            this.logoUrl = this.sanitizer.bypassSecurityTrustUrl(url);
          })
      })
    }
  }

  //TODO : à ajouter au toggle de logo sur l'event de change
  provisionChangeFunction() {
    this.provisionChange.emit();
  }
}
