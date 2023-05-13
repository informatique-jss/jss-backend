import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatLegacyDialogRef as MatDialogRef } from '@angular/material/legacy-dialog';
import { AppService } from '../../../../services/app.service';
import { Notification } from '../../model/Notification';

@Component({
  selector: 'add-notification-dialog',
  templateUrl: './add-notification-dialog.component.html',
  styleUrls: ['./add-notification-dialog.component.css']
})
export class AddNotificationDialogComponent implements OnInit, AfterContentChecked {

  constructor(public dialogRef: MatDialogRef<AddNotificationDialogComponent>,
    private changeDetectorRef: ChangeDetectorRef,
    private appService: AppService,
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
    if (this.notificationForm.valid)
      this.dialogRef.close(this.notification);
    else
      this.appService.displaySnackBar("Veuillez compléter l'ensemble des champs", true, 20);

  }

  onClose(): void {
    this.dialogRef.close(false);
  }
}
