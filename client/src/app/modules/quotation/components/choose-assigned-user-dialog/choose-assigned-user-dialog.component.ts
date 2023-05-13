import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatLegacyDialogRef as MatDialogRef } from '@angular/material/legacy-dialog';
import { Employee } from 'src/app/modules/profile/model/Employee';

@Component({
  selector: 'choose-assigned-user-dialog',
  templateUrl: './choose-assigned-user-dialog.component.html',
  styleUrls: ['./choose-assigned-user-dialog.component.css']
})
export class ChooseAssignedUserDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ChooseAssignedUserDialogComponent>,
    private formBuilder: FormBuilder) { }

  userList: Employee[] = [] as Array<Employee>;
  choosedEmployee: Employee | undefined;
  title: string = "";
  text: string = "";

  assignedForm = this.formBuilder.group({
    choosedEmployee: ['', Validators.required],
  });

  ngOnInit() {
    this.assignedForm.get("choosedEmployee")!.valueChanges.subscribe(
      (newValue) => {
        this.choosedEmployee = newValue as any;
      }
    )
  }

  onConfirm(): void {
    this.dialogRef.close(this.choosedEmployee);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }
}

