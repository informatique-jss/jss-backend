import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { ConfrereService } from 'src/app/modules/quotation/services/confrere.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Department } from '../../../model/Department';
import { ConfirmDialogComponent } from '../../confirm-dialog/confirm-dialog.component';
import { ConfrereDialogComponent } from '../../confreres-dialog/confreres-dialog.component';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-confrere',
  templateUrl: './autocomplete-confrere.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css'],
})
export class AutocompleteConfrereComponent extends GenericAutocompleteComponent<Confrere, Confrere> implements OnInit {

  types: Confrere[] = [] as Array<Confrere>;

  /**
* The label to display
* Mandatory
*/
  @Input() label: string = "Support";

  @Input() filteredDepartment: Department | undefined;

  constructor(private formBuild: UntypedFormBuilder,
    private confrereService: ConfrereService,
    public confrereDialog: MatDialog,
    private userNoteService2: UserNoteService,
    public confirmationDialog: MatDialog,) {
    super(formBuild, userNoteService2)
  }

  displayLabel(object: Confrere): string {
    return object ? object.label + " (" + object.journalType.label + ')' : '';
  }

  searchEntities(value: string): Observable<Confrere[]> {
    return this.confrereService.getConfrereFilteredByDepartmentAndName(this.filteredDepartment, value);
  }

  openConfrereDialog() {
    let dialogConfrere = this.confrereDialog.open(ConfrereDialogComponent, {
      width: '100%'
    });
    if (this.filteredDepartment)
      dialogConfrere.componentInstance.filteredDepartments = this.filteredDepartment;
    dialogConfrere.afterClosed().subscribe(response => {
      if (response && response != null)
        this.model! = response;
      super.optionSelected(this.model!);
    });
  }

  override optionSelected(type: Confrere): void {

    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Confrère non autorisé !",
        content: "Attention, ce confrère n'est pas censé être utilisé ! Rapprochez-vous du service des Annonces Légales avant de l'utiliser !",
        closeActionText: "Annuler",
        validationActionText: "Choisir"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult)
        super.optionSelected(type);
      else
        super.clearField();
    });
  }
}
