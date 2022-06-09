import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Confrere } from '../../model/Confrere';
import { ConfrereFlatten } from '../../model/ConfrereFlatten';
import { ConfrereService } from '../../services/confrere.service';

@Component({
  selector: 'confreres-dialog',
  templateUrl: './confreres-dialog.component.html',
  styleUrls: ['./confreres-dialog.component.css']
})
export class ConfrereDialogComponent implements OnInit {

  displayedColumns: string[] = ['denomination', 'departments', 'discountRate', 'weekDays', 'lastShipmentForPublication',
    'preference', 'mails', 'phones', 'numberOfPrint', 'shippingCosts', 'administrativeFees'];

  confreresFlatten: ConfrereFlatten[] = [] as Array<ConfrereFlatten>;
  confreres: Confrere[] = [] as Array<Confrere>;
  confreresDataSource = new MatTableDataSource<ConfrereFlatten>();

  filterValue: string = "";

  constructor(private confrereService: ConfrereService,
    private confreresDialogRef: MatDialogRef<ConfrereDialogComponent>) { }

  ngOnInit() {
    this.confrereService.getConfreres().subscribe(response => {
      this.confreres = response;
      // Flatten object to display
      if (response && response.length > 0) {
        response.forEach(confrere => {
          let localConfrere = {} as ConfrereFlatten;
          localConfrere.id = confrere.id;
          localConfrere.denomination = confrere.denomination;
          localConfrere.departments = confrere.departments.map(e => e.code).join(", ");
          localConfrere.discountRate = confrere.discountRate;
          localConfrere.weekDays = confrere.weekDays.map(e => e.label).join(", ");
          localConfrere.lastShipmentForPublication = confrere.lastShipmentForPublication;
          localConfrere.preference = confrere.preference;
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
