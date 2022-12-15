import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { ConfrereService } from 'src/app/modules/quotation/services/confrere.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Department } from '../../../model/Department';
import { ConfrereDialogComponent } from '../../confreres-dialog/confreres-dialog.component';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-confrere',
  templateUrl: './autocomplete-confrere.component.html',
  styleUrls: ['./autocomplete-confrere.component.css']
})
export class AutocompleteConfrereComponent extends GenericLocalAutocompleteComponent<Confrere> implements OnInit {

  types: Confrere[] = [] as Array<Confrere>;

  /**
* The label to display
* Mandatory
*/
  @Input() label: string = "Support";

  @Input() filteredDepartments: Department | undefined;

  constructor(private formBuild: UntypedFormBuilder, private confrereService: ConfrereService, public confrereDialog: MatDialog, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  filterEntities(types: Confrere[], value: string): Confrere[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(confrere =>
      confrere.label != undefined
      && (confrere.label.toLowerCase().includes(filterValue)));
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    if (changes.filteredDepartments) {
      if (this.filteredDepartments) {
        let filteredConfrere = [];
        for (let confrere of this.types)
          if (confrere.departments)
            for (let department of confrere.departments)
              if (this.filteredDepartments.id == department.id)
                filteredConfrere.push(confrere);
        this.types = filteredConfrere;
      }
    }
  }

  initTypes(): void {
    this.confrereService.getConfreres().subscribe(response => {
      this.types = response;
      if (this.filteredDepartments) {
        let filteredConfrere = [];
        for (let confrere of this.types)
          if (confrere.departments)
            for (let department of confrere.departments)
              if (this.filteredDepartments.id == department.id)
                filteredConfrere.push(confrere);
        this.types = filteredConfrere;
      }
    })
  }

  displayLabel(object: Confrere): string {
    return object ? object.label + " " + object.journalType.label : '';
  }

  openConfrereDialog() {
    let dialogConfrere = this.confrereDialog.open(ConfrereDialogComponent, {
      width: '100%'
    });
    if (this.filteredDepartments)
      dialogConfrere.componentInstance.filteredDepartments = this.filteredDepartments;
    dialogConfrere.afterClosed().subscribe(response => {
      if (response && response != null)
        this.model! = response;
      this.optionSelected(this.model!);
    });
  }

  public displayConfrere(object: Confrere): string {
    return object ? object.label : '';
  }
}
