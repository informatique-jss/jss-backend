import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { debounceTime, filter, switchMap, tap } from 'rxjs/operators';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { ConfrereService } from 'src/app/modules/quotation/services/confrere.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { ConfrereDialogComponent } from '../../confreres-dialog/confreres-dialog.component';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-confrere',
  templateUrl: './chips-confrere.component.html',
  styleUrls: ['./chips-confrere.component.css']
})
export class ChipsConfrereComponent extends GenericChipsComponent<Confrere> implements OnInit {

  confreres: Confrere[] = [] as Array<Confrere>;
  filteredConfreres: Confrere[] | undefined;
  @ViewChild('confrereInput') confrereInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder, public confrereDialog: MatDialog, private confrereService: ConfrereService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  callOnNgInit(): void {
    if (this.form) {
      this.form.get(this.propertyName)?.valueChanges.pipe(
        filter(res => {
          return res != undefined && res !== null && res.length > 2
        }),
        debounceTime(300),
        tap((value) => {
          this.filteredConfreres = [];
          this.modelChange.emit(this.model);
        }),
        switchMap(value => this.confrereService.getConfrereFilteredByDepartmentAndName(undefined, value)
        )
      ).subscribe(response => {
        this.filteredConfreres = response;
      });
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
