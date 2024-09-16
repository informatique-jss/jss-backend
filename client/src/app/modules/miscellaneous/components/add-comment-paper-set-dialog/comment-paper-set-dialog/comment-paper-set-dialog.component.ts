import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CustomerOrderCommentService } from 'src/app/modules/quotation/services/customer.order.comment.service';

@Component({
  selector: 'app-comment-paper-set-dialog',
  templateUrl: './comment-paper-set-dialog.component.html',
  styleUrls: ['./comment-paper-set-dialog.component.css']
})
export class CommentPaperSetDialogComponent implements OnInit {
  title: string = "";
  comment: string = "";
  isMandatory: boolean = true;

  constructor(
    public dialogRef: MatDialogRef<CommentPaperSetDialogComponent>,
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
