import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { Competitor } from 'src/app/modules/tiers/model/Competitor';
import { CompetitorService } from 'src/app/modules/tiers/services/competitor.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-competitor',
  templateUrl: './chips-competitor.component.html',
  styleUrls: ['./chips-competitor.component.css']
})
export class ChipsCompetitorComponent extends GenericChipsComponent<Competitor> implements OnInit {

  Competitors: Competitor[] = [] as Array<Competitor>;
  filteredCompetitors: Observable<Competitor[]> | undefined;
  @ViewChild('competitorInput') CompetitorInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder, private CompetitorService: CompetitorService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  callOnNgInit(): void {
    this.CompetitorService.getCompetitors().subscribe(response => {
      this.Competitors = response;
    })
    if (this.form)
      this.filteredCompetitors = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this._filterByName(this.Competitors, value))
      );
  }


  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: Competitor): Competitor {
    return object;
  }

  getValueFromObject(object: Competitor): string {
    return object.label;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.label != undefined && input.label.toLowerCase().includes(filterValue));
  }

  addCompetitor(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<Competitor>;
      // Do not add twice
      if (this.model.map(Competitor => Competitor.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.CompetitorInput!.nativeElement.value = '';
    }
  }
}
