import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Confrere } from '../../../quotation/model/Confrere';
import { ConfrereFlatten } from '../../../quotation/model/ConfrereFlatten';
import { ConfrereService } from '../../../quotation/services/confrere.service';
import { SortTableColumn } from '../../model/SortTableColumn';

@Component({
  selector: 'confreres-dialog',
  templateUrl: './confreres-dialog.component.html',
  styleUrls: ['./confreres-dialog.component.css']
})
export class ConfrereDialogComponent implements OnInit {

  displayedColumns2: string[] = ['', '', '', '', '', '',
    '', '', '', '', '', '', '', '', ''];

  confreresFlatten: ConfrereFlatten[] = [] as Array<ConfrereFlatten>;
  confreres: Confrere[] = [] as Array<Confrere>;

  displayedColumns: SortTableColumn[] = [];
  searchText: string | undefined;

  filterValue: string = "";

  constructor(private confrereService: ConfrereService,
    private confreresDialogRef: MatDialogRef<ConfrereDialogComponent>) { }

  ngOnInit() {

    this.confreres.sort(function (a: Confrere, b: Confrere) {
      return (a.boardGrade).localeCompare(b.boardGrade);
    });

    this.confrereService.getConfreres().subscribe(response => {
      this.confreres = response;
      // Flatten object to display
      if (response && response.length > 0) {
        response.forEach(confrere => {
          let localConfrere = {} as ConfrereFlatten;
          localConfrere.id = confrere.id;
          localConfrere.denomination = confrere.label;
          localConfrere.journalType = confrere.journalType;
          localConfrere.departments = confrere.departments.map(e => e.code).join(", ");
          localConfrere.discountRate = (confrere.specialOffers &&
            confrere.specialOffers.length > 0) ? confrere.specialOffers[0].label : "";
          localConfrere.weekDays = confrere.weekDays.map(e => e.label).join(", ");
          localConfrere.lastShipmentForPublication = confrere.lastShipmentForPublication;
          localConfrere.publicationCertificateDocumentGrade = confrere.publicationCertificateDocumentGrade;
          localConfrere.billingGrade = confrere.billingGrade;
          localConfrere.paperGrade = confrere.paperGrade;
          localConfrere.boardGrade = confrere.boardGrade;
          localConfrere.mails = confrere.mails.map(e => e.mail).join(", ");
          localConfrere.phones = confrere.phones.map(e => e.phoneNumber).join(", ");
          localConfrere.numberOfPrint = confrere.numberOfPrint;
          localConfrere.shippingCosts = confrere.shippingCosts;
          localConfrere.administrativeFees = confrere.administrativeFees;
          this.confreresFlatten.push(localConfrere);
        });
        this.confreresFlatten.sort((a, b) => a.discountRate.localeCompare(b.discountRate));


        this.displayedColumns.push({ id: "denomination", fieldName: "denomination", label: "Dénomination" } as SortTableColumn);
        this.displayedColumns.push({ id: "type", fieldName: "journalType.label", label: "Type" } as SortTableColumn);
        this.displayedColumns.push({ id: "departments", fieldName: "departments", label: "Habilitations" } as SortTableColumn);
        this.displayedColumns.push({ id: "discountRate", fieldName: "discountRate", label: "Taux de remise", valueFonction: (element: any, elements: [], column: SortTableColumn, columns: SortTableColumn[]) => { return ((element.specialOffers && element.specialOffers.length > 0) ? element.specialOffers[0].label : "") } } as SortTableColumn);
        this.displayedColumns.push({ id: "weekDays", fieldName: "weekDays", label: "Jours de parution" } as SortTableColumn);
        this.displayedColumns.push({ id: "lastShipmentForPublication", fieldName: "lastShipmentForPublication", label: "Dernier envoi pour parution" } as SortTableColumn);
        this.displayedColumns.push({ id: "publicationCertificateDocumentGrade", displayAsGrade: true, fieldName: "publicationCertificateDocumentGrade", label: "Préférence attestation de parution" } as SortTableColumn);
        this.displayedColumns.push({ id: "billingGrade", fieldName: "billingGrade", displayAsGrade: true, label: "Préférence facturation" } as SortTableColumn);
        this.displayedColumns.push({ id: "paperGrade", fieldName: "paperGrade", displayAsGrade: true, label: "Préférence journal" } as SortTableColumn);
        this.displayedColumns.push({ id: "boardGrade", fieldName: "boardGrade", displayAsGrade: true, label: "Préférence direction" } as SortTableColumn);
        this.displayedColumns.push({ id: "mails", fieldName: "mails", label: "Mails" } as SortTableColumn);
        this.displayedColumns.push({ id: "phones", fieldName: "phones", label: "Téléphones" } as SortTableColumn);
        this.displayedColumns.push({ id: "numberOfPrint", fieldName: "numberOfPrint", label: "Tirage" } as SortTableColumn);
        this.displayedColumns.push({ id: "shippingCosts", fieldName: "shippingCosts", label: "Frais de port" } as SortTableColumn);
        this.displayedColumns.push({ id: "administrativeFees", fieldName: "administrativeFees", label: "Frais administratifs" } as SortTableColumn);
      }
    })
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }

  chooseConfrere(confrereFlatten: ConfrereFlatten) {
    let outConfrere = null;
    this.confreres.forEach(confrere => {
      if (confrere.id == confrereFlatten.id)
        outConfrere = confrere;
      return;
    });
    this.confreresDialogRef.close(outConfrere);
  }

  closeDialog() {
    this.confreresDialogRef.close(null);
  }
}
