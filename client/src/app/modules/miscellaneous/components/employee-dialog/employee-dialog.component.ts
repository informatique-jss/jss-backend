import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatLegacyDialogRef as MatDialogRef } from '@angular/material/legacy-dialog';
import { Employee } from '../../../profile/model/Employee';

@Component({
  selector: 'app-employee-dialog',
  templateUrl: './employee-dialog.component.html',
  styleUrls: ['./employee-dialog.component.css']
})
export class EmployeeDialogComponent implements OnInit {

  title: string = "";
  content: string = "";
  employee: Employee | undefined;
  closeActionText: string = "";
  validationActionText: string = "";

  constructor(public dialogRef: MatDialogRef<EmployeeDialogComponent>,
    private formBuilder: FormBuilder,
  ) { }

  employeeForm = this.formBuilder.group({});

  ngOnInit() {
  }

  onConfirm(): void {
    this.dialogRef.close(this.employee);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }


}
