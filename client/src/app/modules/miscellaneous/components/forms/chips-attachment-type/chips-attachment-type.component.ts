import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { AttachmentType } from '../../../model/AttachmentType';
import { AttachmentTypeService } from '../../../services/attachment.type.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'chips-attachment-type',
  templateUrl: './chips-attachment-type.component.html',
  styleUrls: ['./chips-attachment-type.component.css']
})
export class ChipsAttachmentTypeComponent extends GenericChipsComponent<AttachmentType> implements OnInit {

  attachmentTypes: AttachmentType[] = [] as Array<AttachmentType>;
  filteredAttachmentTypes: Observable<AttachmentType[]> | undefined;
  @ViewChild('attachmentTypeInput') AttachmentTypeInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder,
    private attachmentTypeService: AttachmentTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  callOnNgInit(): void {
    this.attachmentTypeService.getAttachmentTypes().subscribe(response => {
      this.attachmentTypes = response.filter(attachmentType => !attachmentType.isHiddenFromUser);
    })
    if (this.form)
      this.filteredAttachmentTypes = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this._filterByName(this.attachmentTypes, value))
      );
  }

  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: AttachmentType): AttachmentType {
    return object;
  }

  getValueFromObject(object: AttachmentType): string {
    return object.label;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.label != undefined && input.label.toLowerCase().includes(filterValue));
  }

  addAttachmentType(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<AttachmentType>;
      // Do not add twice
      if (this.model.map(AttachmentType => AttachmentType.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.AttachmentTypeInput!.nativeElement.value = '';
    }
  }
}
