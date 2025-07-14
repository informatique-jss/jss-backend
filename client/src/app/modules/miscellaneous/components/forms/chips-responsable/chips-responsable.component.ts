import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { debounceTime, distinctUntilChanged, filter, switchMap, tap } from 'rxjs';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { ResponsableService } from 'src/app/modules/tiers/services/responsable.service';
import { AppService } from 'src/app/services/app.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-responsable',
  templateUrl: './chips-responsable.component.html',
  styleUrls: ['./chips-responsable.component.css']
})
export class ChipsResponsableComponent extends GenericChipsComponent<Responsable> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder,
    private appService3: AppService, private responsableService: ResponsableService) {
    super(formBuild, appService3)
  }
  filteredResponsable: Responsable[] | undefined;
  @ViewChild('responsableInput') responsableInput: ElementRef<HTMLInputElement> | undefined;

  callOnNgInit(): void {
    if (this.form)
      this.form.get(this.propertyName)?.valueChanges.pipe(
        filter(res => {
          return res != undefined && res !== null && res.length >= 2
        }),
        distinctUntilChanged(),
        debounceTime(300),
        tap((value) => {
          this.filteredResponsable = [];
          this.modelChange.emit(this.model);
        }),
        switchMap(value => this.responsableService.getResponsables(value)
        )
      ).subscribe(response => {
        this.filteredResponsable = response;
      });
  }

  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: Responsable): Responsable {
    return object;
  }

  getValueFromObject(object: Responsable): string {
    return object.firstname + " " + object.lastname;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.firstname != undefined && input.firstname.toLowerCase().includes(filterValue) || input.lastname != undefined && input.lastname.toLowerCase().includes(filterValue));
  }

  addResponsable(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<Responsable>;
      if (this.model.map(responsable => responsable.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.responsableInput!.nativeElement.value = '';
    }
  }
}
