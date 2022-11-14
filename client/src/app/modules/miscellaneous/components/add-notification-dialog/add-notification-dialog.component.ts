import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Notification } from '../../model/Notification';

@Component({
  selector: 'add-notification-dialog',
  templateUrl: './add-notification-dialog.component.html',
  styleUrls: ['./add-notification-dialog.component.css']
})
export class AddNotificationDialogComponent implements OnInit, AfterContentChecked {

  constructor(public dialogRef: MatDialogRef<AddNotificationDialogComponent>,
    private changeDetectorRef: ChangeDetectorRef,
    private formBuilder: FormBuilder,
  ) { }

  notification: Notification = {} as Notification;

  notificationForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  onConfirm(): void {
    this.dialogRef.close(this.notification);
  }

  onClose(): void {
    this.dialogRef.close(false);
  }
}
