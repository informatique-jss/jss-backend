import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { TypeDocument } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDocument';
import { TypeDocumentService } from '../../../services/guichet-unique/type.document.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'chips-type-document',
  templateUrl: './chips-type-document.component.html',
  styleUrls: ['./chips-type-document.component.css']
})
export class ChipsTypeDocumentComponent extends GenericChipsComponent<TypeDocument> implements OnInit {

  typeDocuments: TypeDocument[] = [] as Array<TypeDocument>;
  filteredTypeDocuments: Observable<TypeDocument[]> | undefined;
  @ViewChild('typeDocumentInput') TypeDocumentInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder,
    private typeDocumentService: TypeDocumentService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  callOnNgInit(): void {
    this.typeDocumentService.getTypeDocument().subscribe(response => {
      this.typeDocuments = response;
    })
    if (this.form)
      this.filteredTypeDocuments = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this._filterByName(this.typeDocuments, value))
      );
  }

  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: TypeDocument): TypeDocument {
    return object;
  }

  getValueFromObject(object: TypeDocument): string {
    return object.label;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.label != undefined && input.label.toLowerCase().includes(filterValue));
  }

  addTypeDocument(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<TypeDocument>;
      // Do not add twice
      if (this.model.map(TypeDocument => TypeDocument.code).indexOf(event.option.value.code) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.code)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.TypeDocumentInput!.nativeElement.value = '';
    }
  }
}
