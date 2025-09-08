import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ActiveDirectoryGroup } from 'src/app/modules/miscellaneous/model/ActiveDirectoryGroup';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from '../../../../services/app.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
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
    private userPreferenceService: UserPreferenceService,
    private constantService: ConstantService
  ) { }

  employeeForm = this.formBuilder.group({});

  adSales: ActiveDirectoryGroup | undefined;
  currentEmployee: Employee | undefined;
  editMode: boolean = false;

  saveObservableSubscription: Subscription = new Subscription;

  ngOnInit() {
    this.appService.changeHeaderTitle("Mon profil");
    this.employeeService.getCurrentEmployee().subscribe(response => {
      this.currentEmployee = response;
      this.appService.changeHeaderTitle("Mon profil : " + this.currentEmployee.firstname + " " + this.currentEmployee.lastname);
    })

    this.adSales = this.constantService.getActiveDirectoryGroupSales();

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveEmployee()
        else
          this.editEmployee()
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
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

  deleteSearchPreferences() {
    this.userPreferenceService.deleteSearchPreferences();
    this.appService.displaySnackBar("Recherches effacées", false, 10);
  }
}
