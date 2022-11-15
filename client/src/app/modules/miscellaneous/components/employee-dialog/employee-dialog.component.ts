import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { LoginService } from '../../../../routing/login-dialog/login.service';
import { Employee } from '../../../profile/model/Employee';
import { EmployeeService } from '../../../profile/services/employee.service';

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
    private loginService: LoginService,
    private employeeService: EmployeeService
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

  chooseMyself() {
    this.employeeService.getCurrentEmployee().subscribe(response => {
      this.employee = response;
    })
  }
}
