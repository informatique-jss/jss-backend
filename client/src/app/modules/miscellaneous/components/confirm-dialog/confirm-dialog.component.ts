import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.css']
})
export class ConfirmDialogComponent implements OnInit {

  title: string = "";
  content: string = "";
  closeActionText: string = "";
  validationActionText: string = "";

  constructor(public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
    if (this.data.title)
      this.title = this.data.title;
    if (this.data.content)
      this.content = this.data.content;
    if (this.data.closeActionText)
      this.closeActionText = this.data.closeActionText;
    if (this.data.validationActionText)
      this.validationActionText = this.data.validationActionText;
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }

  onClose(): void {
    this.dialogRef.close(false);
  }

}
function MD_DIALOG_DATA(MD_DIALOG_DATA: any) {
  throw new Error('Function not implemented.');
}

