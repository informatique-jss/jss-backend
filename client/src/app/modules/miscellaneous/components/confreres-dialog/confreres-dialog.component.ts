import { Component, Input, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Confrere } from '../../../quotation/model/Confrere';
import { JournalType } from '../../../quotation/model/JournalType';
import { ConfrereService } from '../../../quotation/services/confrere.service';
import { Department } from '../../model/Department';
import { SortTableColumn } from '../../model/SortTableColumn';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'confreres-dialog',
  templateUrl: './confreres-dialog.component.html',
  styleUrls: ['./confreres-dialog.component.css']
})
export class ConfrereDialogComponent implements OnInit {

  displayedColumns2: string[] = ['', '', '', '', '', '',
    '', '', '', '', '', '', '', '', ''];

  confreres: Confrere[] = [] as Array<Confrere>;

  displayedColumns: SortTableColumn<Confrere>[] = [];
  searchText: string | undefined;

  filteredDepartments: Department | undefined;

  @Input() journalType: JournalType | undefined;

  filterValue: string = "";

  constructor(private confrereService: ConfrereService,
    public confirmationDialog: MatDialog,
    private confreresDialogRef: MatDialogRef<ConfrereDialogComponent>) { }

  ngOnInit() {

    this.displayedColumns = [];
    this.confrereService.getConfreres().subscribe(response => {
      this.confreres = response.sort((a, b) => {
        if (!a.boardGrade && !b.boardGrade)
          return 0;
        if (!a.boardGrade && b.boardGrade)
          return -1;
        if (a.boardGrade && !b.boardGrade)
          return 1;
        return (a.boardGrade).localeCompare(b.boardGrade)
      }
      ).filter(confrere => this.journalType == undefined || this.journalType.id == confrere.journalType.id);

      if (this.filteredDepartments) {
        let filteredConfrere = [];
        for (let confrere of this.confreres)
          if (confrere.departments)
            for (let department of confrere.departments)
              if (this.filteredDepartments.id == department.id)
                filteredConfrere.push(confrere);
        this.confreres = filteredConfrere;
      }

      this.displayedColumns.push({ id: "denomination", fieldName: "label", label: "Dénomination" } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "type", fieldName: "journalType.label", label: "Type" } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "departments", fieldName: "departments", label: "Habilitations", valueFonction: (element: Confrere, column: SortTableColumn<Confrere>) => { return ((element.departments) ? element.departments.map((e: { code: any; }) => e.code).join(", ") : "") } } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "discountRate", fieldName: "discountRate", label: "Taux de remise (%)" } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "weekDays", fieldName: "weekDays", label: "Jours de parution", valueFonction: (element: Confrere, column: SortTableColumn<Confrere>) => { return ((element.departments) ? element.weekDays.map((e: { label: any; }) => e.label).join(", ") : "") } } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "lastShipmentForPublication", fieldName: "lastShipmentForPublication", label: "Dernier envoi pour parution" } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "publicationCertificateDocumentGrade", displayAsGrade: true, fieldName: "publicationCertificateDocumentGrade", label: "Préférence attestation de parution" } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "billingGrade", fieldName: "billingGrade", displayAsGrade: true, label: "Préférence facturation" } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "paperGrade", fieldName: "paperGrade", displayAsGrade: true, label: "Préférence journal" } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "boardGrade", fieldName: "boardGrade", displayAsGrade: true, label: "Préférence direction" } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: Confrere, column: SortTableColumn<Confrere>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: Confrere, column: SortTableColumn<Confrere>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "numberOfPrint", fieldName: "numberOfPrint", label: "Tirage" } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "shippingCosts", fieldName: "shippingCosts", label: "Frais de port" } as SortTableColumn<Confrere>);
      this.displayedColumns.push({ id: "administrativeFees", fieldName: "administrativeFees", label: "Frais administratifs" } as SortTableColumn<Confrere>);

    })
  }

  filterPredicate(record: any, filter: any) {
    if (filter == "")
      return true;
    let search: string = record.departments.map((e: { code: any; }) => e.code).join(", ");
    search += record.label;
    search += record.weekDays.map((e: { label: any; }) => e.label).join(", ");
    return search.toLowerCase().indexOf(filter.toLowerCase()) >= 0;
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }

  chooseConfrere(confrereFlatten: Confrere) {
    let outConfrere = {} as Confrere;
    this.confreres.forEach(confrere => {
      if (confrere.id == confrereFlatten.id)
        outConfrere = confrere;
      return;
    });

    if (outConfrere.doNotUse) {
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
          this.confreresDialogRef.close(outConfrere!);
      });
    } else {
      this.confreresDialogRef.close(outConfrere!);
    }
  }

  closeDialog() {
    this.confreresDialogRef.close(null);
  }
}
