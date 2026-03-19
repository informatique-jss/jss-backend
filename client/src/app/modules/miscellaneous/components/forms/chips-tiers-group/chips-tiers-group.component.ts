import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { map, Observable, startWith } from 'rxjs';
import { TiersGroupService } from 'src/app/modules/quotation/services/tiers-group.service';
import { TiersGroup } from 'src/app/modules/tiers/model/TiersGroup';
import { AppService } from 'src/app/services/app.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-tiers-group',
  templateUrl: './chips-tiers-group.component.html',
  styleUrls: ['./chips-tiers-group.component.css']
})
export class ChipsTiersGroupComponent extends GenericChipsComponent<TiersGroup> implements OnInit {

  tiersGroups: TiersGroup[] = [] as Array<TiersGroup>;
  filteredTiersGroups: Observable<TiersGroup[]> | undefined;
  @ViewChild('tiersGroupInput') tiersGroupInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder, public specialOfferDialog: MatDialog,
    private tiersGroupService: TiersGroupService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  callOnNgInit(): void {
    this.tiersGroupService.getTiersGroups().subscribe(response => {
      this.tiersGroups = response;
    })
    if (this.form)
      this.filteredTiersGroups = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this._filterByName(this.tiersGroups, value))
      );
  }

  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: TiersGroup): TiersGroup {
    return object;
  }

  getValueFromObject(object: TiersGroup): string {
    return object.label;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.label != undefined && input.label.toLowerCase().includes(filterValue));
  }

  addTiersGroup(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<TiersGroup>;
      // Do not add twice
      if (this.model.map(tiersGroup => tiersGroup.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.tiersGroupInput!.nativeElement.value = '';
    }
  }
}
