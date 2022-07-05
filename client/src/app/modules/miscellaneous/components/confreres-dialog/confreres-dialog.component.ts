import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Confrere } from '../../../quotation/model/Confrere';
import { ConfrereFlatten } from '../../../quotation/model/ConfrereFlatten';
import { ConfrereService } from '../../../quotation/services/confrere.service';

@Component({
  selector: 'confreres-dialog',
  templateUrl: './confreres-dialog.component.html',
  styleUrls: ['./confreres-dialog.component.css']
})
export class ConfrereDialogComponent implements OnInit {

  displayedColumns: string[] = ['denomination', 'type', 'departments', 'discountRate', 'weekDays', 'lastShipmentForPublication',
    'publicationCertificateDocumentGrade', 'billingGrade', 'paperGrade', 'boardGrade', 'mails', 'phones', 'numberOfPrint', 'shippingCosts', 'administrativeFees'];

  confreresFlatten: ConfrereFlatten[] = [] as Array<ConfrereFlatten>;
  confreres: Confrere[] = [] as Array<Confrere>;
  confreresDataSource = new MatTableDataSource<ConfrereFlatten>();
  @ViewChild(MatSort) sort!: MatSort;

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
          localConfrere.discountRate = confrere.discountRate;
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
        this.confreresDataSource.data = this.confreresFlatten;
      }
    })

    setTimeout(() => {
      this.confreresDataSource.sort = this.sort;
      this.confreresDataSource.sortingDataAccessor = (confrere: ConfrereFlatten, property) => {
        switch (property) {
          case 'id': return confrere.id;
          case 'denomination': return confrere.denomination;
          case 'journalType': return confrere.journalType.label;
          case 'departments': return confrere.departments;
          case 'discountRate': return confrere.discountRate;
          case 'weekDays': return confrere.weekDays;
          case 'lastShipmentForPublication': return confrere.lastShipmentForPublication;
          case 'publicationCertificateDocumentGrade': return confrere.publicationCertificateDocumentGrade;
          case 'billingGrade': return confrere.billingGrade;
          case 'paperGrade': return confrere.paperGrade;
          case 'boardGrade': return confrere.boardGrade;
          case 'mails': return confrere.mails;
          case 'phones': return confrere.phones;
          case 'numberOfPrint': return confrere.numberOfPrint;
          case 'shippingCosts': return confrere.shippingCosts;
          case 'administrativeFees': return confrere.administrativeFees;
          default: return confrere.denomination;
        }
      };

      this.confreresDataSource.filterPredicate = (data: any, filter) => {
        const dataStr = JSON.stringify(data).toLowerCase();
        return dataStr.indexOf(filter) != -1;
      }
    });
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    filterValue = filterValue.toLowerCase();
    this.confreresDataSource.filter = filterValue;
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
