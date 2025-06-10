import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'counter-part-dialog',
  templateUrl: './counter-part-dialog.component.html',
  styleUrls: ['./counter-part-dialog.component.css']
})
export class CounterPartDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<CounterPartDialogComponent>, private formBuilder: FormBuilder) { }

  label: string = "";
  counterPartDateTime: Date = new Date();
  title: string = "Date de la contre-passe";

  counterPartForm = this.formBuilder.group({});

  ngOnInit() {
  }

  onConfirm(): void {
    this.dialogRef.close(this.counterPartDateTime);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }
}
