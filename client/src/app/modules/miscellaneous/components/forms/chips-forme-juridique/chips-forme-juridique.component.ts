import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Observable, map, startWith } from 'rxjs';
import { FormeJuridique } from 'src/app/modules/quotation/model/guichet-unique/referentials/FormeJuridique';
import { FormeJuridiqueService } from '../../../services/guichet-unique/forme.juridique.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'chips-forme-juridique',
  templateUrl: './chips-forme-juridique.component.html',
  styleUrls: ['./chips-forme-juridique.component.css']
})
export class ChipsFormeJuridiqueComponent extends GenericChipsComponent<FormeJuridique> implements OnInit {

  formeJuridiques: FormeJuridique[] = [] as Array<FormeJuridique>;
  filteredFormeJuridiques: Observable<FormeJuridique[]> | undefined;
  @ViewChild('formeJuridiqueInput') FormeJuridiqueInput: ElementRef<HTMLInputElement> | undefined;
  @Input() hint: string | undefined;

  constructor(private formBuild: UntypedFormBuilder,
    private formeJuridiqueService: FormeJuridiqueService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  callOnNgInit(): void {
    this.formeJuridiqueService.getFormeJuridique().subscribe(response => {
      this.formeJuridiques = response;
    })
    if (this.form)
      this.filteredFormeJuridiques = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this._filterByName(this.formeJuridiques, value))
      );
  }

  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: FormeJuridique): FormeJuridique {
    return object;
  }

  getValueFromObject(object: FormeJuridique): string {
    return object.label;
  }

  private _filterByName(inputList: FormeJuridique[], value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: FormeJuridique) => input.label != undefined && input.label.toLowerCase().includes(filterValue) || input.labelShort != undefined && input.labelShort.toLowerCase().includes(filterValue));
  }

  addFormeJuridique(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<FormeJuridique>;
      // Do not add twice
      if (this.model.map(formeJuridique => formeJuridique.code).indexOf(event.option.value.code) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.code)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.FormeJuridiqueInput!.nativeElement.value = '';
    }
  }
}
