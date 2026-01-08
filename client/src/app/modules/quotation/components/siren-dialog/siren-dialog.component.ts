import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-siren-dialog',
  templateUrl: './siren-dialog.component.html',
  styleUrls: ['./siren-dialog.component.css']
})
export class SiretDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<SiretDialogComponent>, private formBuilder: FormBuilder) { }

  label: string = "";
  title: string = "";
  siret: string = "";

  amountForm = this.formBuilder.group({});

  ngOnInit() {
  }

  onConfirm(): void {
    if (this.siret && (this.siret as any).siret) {
      this.siret = (this.siret as any).siret;
      return;
    }

    this.dialogRef.close(this.siret);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }
}
