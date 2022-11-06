import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DocumentExtensionService } from 'src/app/modules/miscellaneous/services/guichet-unique/document.extension.service';
import { DocumentExtension } from 'src/app/modules/quotation/model/guichet-unique/referentials/DocumentExtension';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-document-extension',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupDocumentExtensionComponent extends GenericRadioGroupComponent<DocumentExtension> implements OnInit {
  types: DocumentExtension[] = [] as Array<DocumentExtension>;

  constructor(
    private formBuild: UntypedFormBuilder, private DocumentExtensionService: DocumentExtensionService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.DocumentExtensionService.getDocumentExtension().subscribe(response => { this.types = response })
  }
}
