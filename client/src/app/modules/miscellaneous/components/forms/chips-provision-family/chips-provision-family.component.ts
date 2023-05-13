import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatLegacyAutocompleteSelectedEvent as MatAutocompleteSelectedEvent } from '@angular/material/legacy-autocomplete';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { ProvisionFamilyType } from 'src/app/modules/quotation/model/ProvisionFamilyType';
import { ProvisionFamilyTypeService } from 'src/app/modules/quotation/services/provision.family.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-provision-family',
  templateUrl: './chips-provision-family.component.html',
  styleUrls: ['./chips-provision-family.component.css']
})
export class ChipsProvisionFamilyTypeComponent extends GenericChipsComponent<ProvisionFamilyType> implements OnInit {
  ProvisionFamilyTypes: ProvisionFamilyType[] = [] as Array<ProvisionFamilyType>;
  filteredProvisionFamilyTypes: Observable<ProvisionFamilyType[]> | undefined;
  @ViewChild('provisionFamilyInput') ProvisionFamilyTypeInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder, private provisionFamilyTypeService: ProvisionFamilyTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  callOnNgInit(): void {
    this.provisionFamilyTypeService.getProvisionFamilyTypes().subscribe(response => {
      this.ProvisionFamilyTypes = response;
    })
    if (this.form)
      this.filteredProvisionFamilyTypes = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this._filterByName(this.ProvisionFamilyTypes, value))
      );
  }


  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: ProvisionFamilyType): ProvisionFamilyType {
    return object;
  }

  getValueFromObject(object: ProvisionFamilyType): string {
    return object.label;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.label != undefined && input.label.toLowerCase().includes(filterValue));
  }

  addProvisionFamilyType(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<ProvisionFamilyType>;
      // Do not add twice
      if (this.model.map(ProvisionFamilyType => ProvisionFamilyType.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.ProvisionFamilyTypeInput!.nativeElement.value = '';
    }
  }
}
