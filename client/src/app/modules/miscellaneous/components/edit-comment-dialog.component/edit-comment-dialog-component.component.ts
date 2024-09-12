import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'edit-comment-dialog.component',
  templateUrl: './edit-comment-dialog.component.html',
  styleUrls: ['./edit-comment-dialog.component.css']
})
export class EditCommentDialogComponent {

  comment: string = "";
  isMandatory: boolean = true;

  constructor(
    public dialogRef: MatDialogRef<EditCommentDialogComponent>,
    private formBuilder: FormBuilder
  ) {
  }

  commentForm = this.formBuilder.group({});

  onConfirm() {
    if (this.commentForm.valid) {
      this.dialogRef.close(this.comment);
    }
  }

  onClose() {
    this.dialogRef.close(null);
  }

}
