import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { debounceTime, filter, switchMap, tap } from 'rxjs';
import { GUICHET_UNIQUE_BASE_URL } from 'src/app/libs/Constants';
import { Provision } from 'src/app/modules/quotation/model/Provision';
import { FormaliteGuichetUnique } from 'src/app/modules/quotation/model/guichet-unique/FormaliteGuichetUnique';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { FormaliteGuichetUniqueService } from '../../../services/formalite.guichet.unique.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-formalite-guichet-unique',
  templateUrl: './chips-formalite-guichet-unique.component.html',
  styleUrls: ['./chips-formalite-guichet-unique.component.css']
})
export class ChipsFormaliteGuichetUniqueComponent extends GenericChipsComponent<FormaliteGuichetUnique> implements OnInit {

  @Input() provision: Provision | undefined;
  formaliteGuichetUniques: FormaliteGuichetUnique[] = [] as Array<FormaliteGuichetUnique>;
  filteredFormaliteGuichetUniques: FormaliteGuichetUnique[] | undefined;
  @ViewChild('formaliteGuichetUniqueInput') formaliteGuichetUniqueInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder,
    private formaliteGuichetUniqueService: FormaliteGuichetUniqueService,
    private userNoteService2: UserNoteService,) {
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
          this.filteredFormaliteGuichetUniques = [];
          this.modelChange.emit(this.model);
        }),
        switchMap(value => this.formaliteGuichetUniqueService.getFormaliteGuichetUniqueServiceByReference(value, this.provision!)
        )
      ).subscribe(response => {
        this.filteredFormaliteGuichetUniques = response;
      });
    }
  }


  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: FormaliteGuichetUnique): FormaliteGuichetUnique {
    return object;
  }

  getValueFromObject(object: FormaliteGuichetUnique): string {
    return object.id + "";
  }

  addFormaliteGuichetUnique(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<FormaliteGuichetUnique>;
      // Do not add twice
      if (this.model.map(formaliteGuichetUnique => formaliteGuichetUnique.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.formaliteGuichetUniqueInput!.nativeElement.value = '';
    }
  }

  openGuichetUnique(event: any, formalite: FormaliteGuichetUnique) {
    window.open(GUICHET_UNIQUE_BASE_URL + formalite.id, "_blank");
    return;
  }
}
