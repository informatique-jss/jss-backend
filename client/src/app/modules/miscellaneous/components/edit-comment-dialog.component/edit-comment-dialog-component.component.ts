import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'edit-comment-dialog.component',
  templateUrl: './edit-comment-dialog.component.html',
  styleUrls: ['./edit-comment-dialog.component.css']
})
export class EditCommentDialogComponent implements OnInit {
  title: string = "Editer le commentaire"
  comment: string = "";
  isMandatory: boolean = true;

  constructor(
    public dialogRef: MatDialogRef<EditCommentDialogComponent>,
    private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
  }

  commentForm = this.formBuilder.group({});

  ngOnInit() {
    if (this.data.title)
      this.title = this.data.title;
  }
  onConfirm() {
    if (this.commentForm.valid) {
      this.dialogRef.close(this.comment);
    }
  }

  onClose() {
    this.dialogRef.close(null);
  }

}
