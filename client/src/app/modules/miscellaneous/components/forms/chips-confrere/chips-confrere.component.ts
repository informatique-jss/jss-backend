import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, ValidatorFn, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { ConfrereService } from 'src/app/modules/quotation/services/confrere.service';
import { ConfrereDialogComponent } from '../../confreres-dialog/confreres-dialog.component';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-confrere',
  templateUrl: './chips-confrere.component.html',
  styleUrls: ['./chips-confrere.component.css']
})
export class ChipsConfrereComponent extends GenericChipsComponent<Confrere> implements OnInit {
  confreres: Confrere[] = [] as Array<Confrere>;
  filteredConfreres: Observable<Confrere[]> | undefined;
  @ViewChild('confrereInput') confrereInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder,
    public confrereDialog: MatDialog,
    private confrereService: ConfrereService) {
    super(formBuild);
  }

  ngOnInit() {
    this.confrereService.getConfreres().subscribe(response => {
      this.confreres = response;
    })
    if (this.form != undefined) {
      let validators: ValidatorFn[] = [] as Array<ValidatorFn>;
      if (this.isMandatory) {
        if (this.conditionnalRequired != undefined) {
          validators.push(this.checkFieldFilledIfIsConditionalRequired());
        } else {
          validators.push(Validators.required);
        }
      }

      if (this.customValidators != undefined && this.customValidators != null && this.customValidators.length > 0)
        validators.push(...this.customValidators);

      this.form.addControl(this.propertyName, this.formBuild.control('', validators));

      this.filteredConfreres = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => (typeof value === 'string') ? this._filterByName(this.confreres, value) : [])
      );

      this.form.markAllAsTouched();
    }
  }


  validateInput(value: string): boolean {
    return true;
  }


  openConfrereDialog() {
    let dialogConfrere = this.confrereDialog.open(ConfrereDialogComponent, {
      width: '100%'
    });
    dialogConfrere.afterClosed().subscribe(response => {
      if (!this.confreres)
        this.confreres = [] as Array<Confrere>;
      if (this.model!.map(confrere => confrere.id).indexOf(response.id) < 0) {
        this.model!.push(response);
        this.modelChange.emit(this.model);
      }
    });
  }

  setValueToObject(value: string, object: Confrere): Confrere {
    return object;
  }

  getValueFromObject(object: Confrere): string {
    return object.label;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.denomination != undefined && input.denomination.toLowerCase().includes(filterValue));
  }

  addConfrere(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<Confrere>;
      // Do not add twice
      if (this.model.map(confrere => confrere.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.confrereInput!.nativeElement.value = '';
    }
  }
}
