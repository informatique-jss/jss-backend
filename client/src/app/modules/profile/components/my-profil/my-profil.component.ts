import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../services/app.service';
import { Employee } from '../../model/Employee';
import { EmployeeService } from '../../services/employee.service';

@Component({
  selector: 'my-profil',
  templateUrl: './my-profil.component.html',
  styleUrls: ['./my-profil.component.css']
})
export class MyProfilComponent implements OnInit {

  constructor(
    private appService: AppService,
    private employeeService: EmployeeService,
    private formBuilder: FormBuilder,
  ) { }

  employeeForm = this.formBuilder.group({});

  currentEmployee: Employee | undefined;
  editMode: boolean = false;

  ngOnInit() {
    this.appService.changeHeaderTitle("Mon profil");
    this.employeeService.getCurrentEmployee().subscribe(response => {
      this.currentEmployee = response;
      this.appService.changeHeaderTitle("Mon profil : " + this.currentEmployee.firstname + " " + this.currentEmployee.lastname);
    })
  }

  editEmployee() {
    this.editMode = true;
  }

  saveEmployee() {
    if (this.employeeForm.valid && this.currentEmployee)
      this.employeeService.addOrUpdateEmployee(this.currentEmployee).subscribe(response => {
        this.editMode = false;
        this.currentEmployee = response;
      });
  }
}
