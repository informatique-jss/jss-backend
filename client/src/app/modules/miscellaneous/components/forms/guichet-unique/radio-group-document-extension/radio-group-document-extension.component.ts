import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DocumentExtensionService } from 'src/app/modules/miscellaneous/services/guichet-unique/document.extension.service';
import { DocumentExtension } from 'src/app/modules/quotation/model/guichet-unique/referentials/DocumentExtension';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-document-extension',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupDocumentExtensionComponent extends GenericRadioGroupComponent<DocumentExtension> implements OnInit {
  types: DocumentExtension[] = [] as Array<DocumentExtension>;

  constructor(
    private formBuild: UntypedFormBuilder, private DocumentExtensionService: DocumentExtensionService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.DocumentExtensionService.getDocumentExtension().subscribe(response => { this.types = response })
  }
}
