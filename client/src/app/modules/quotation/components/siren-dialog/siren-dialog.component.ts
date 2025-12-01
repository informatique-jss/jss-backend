import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-siren-dialog',
  templateUrl: './siren-dialog.component.html',
  styleUrls: ['./siren-dialog.component.css']
})
export class SirenDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<SirenDialogComponent>, private formBuilder: FormBuilder) { }

  label: string = "";
  title: string = "";
  siren: string = "";

  amountForm = this.formBuilder.group({});

  ngOnInit() {
  }

  onConfirm(): void {
    this.dialogRef.close(this.siren);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }
}
