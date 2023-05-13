import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatLegacyDialog as MatDialog } from '@angular/material/legacy-dialog';
import { displayInTeams } from 'src/app/libs/MailHelper';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeDialogComponent } from '../employee-dialog/employee-dialog.component';

@Component({
  selector: 'avatar-chip',
  templateUrl: './avatar-chip.component.html',
  styleUrls: ['./avatar-chip.component.css']
})
export class AvatarChipComponent implements OnInit {

  @Input() employee: Employee | undefined;
  @Output() onChangeAssigne: EventEmitter<Employee> = new EventEmitter<Employee>();
  @Input() disableEmployeeDialog = false;

  constructor(
    private employeeDialog: MatDialog
  ) { }

  ngOnInit() {
  }

  displayInTeams = displayInTeams;

  changeEmployee() {
    if (!this.disableEmployeeDialog) {
      let changeEmployeeDialogRef = this.employeeDialog.open(EmployeeDialogComponent, {
        width: '50%'
      });
      changeEmployeeDialogRef.componentInstance.employee = this.employee;
      changeEmployeeDialogRef.componentInstance.content = "Indiquez ici le nouveau collaborateur à affecter :";
      changeEmployeeDialogRef.componentInstance.closeActionText = "Annuler";
      changeEmployeeDialogRef.componentInstance.validationActionText = "Affecter";
      changeEmployeeDialogRef.afterClosed().subscribe(response => {
        if (response)
          this.onChangeAssigne.emit(response);
      })
    }
  }

}
