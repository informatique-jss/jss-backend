import { Component, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { AttachmentType } from '../../../model/AttachmentType';
import { AttachmentTypeService } from '../../../services/attachment.type.service';
import { ConstantService } from '../../../services/constant.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-attachment-type',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteAttachmentTypeComponent extends GenericLocalAutocompleteComponent<AttachmentType> implements OnInit {

  types: AttachmentType[] = [] as Array<AttachmentType>;

  constructor(private formBuild: UntypedFormBuilder, private attachmentTypeService: AttachmentTypeService,
    private constantService: ConstantService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
  }

  filterEntities(types: AttachmentType[], value: string): AttachmentType[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(attachmentType =>
      attachmentType.label != undefined && (attachmentType.label.toLowerCase().includes(filterValue)));
  }

  initTypes(): void {
    this.attachmentTypeService.getAttachmentTypes().subscribe(response => {
      response.sort((a: AttachmentType, b: AttachmentType) => a.label.localeCompare(b.label))
      this.types = response;
    });
  }
}
