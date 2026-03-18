import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { ActiveDirectoryGroupService } from 'src/app/modules/miscellaneous/services/active.directory.group.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { CustomerOrderAssignationService } from 'src/app/modules/quotation/services/customer.assignation.service';
import { CustomerOrderService } from 'src/app/modules/quotation/services/customer.order.service';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'app-assign-new-order-dialog',
  templateUrl: './assign-new-order-dialog.component.html',
  styleUrls: ['./assign-new-order-dialog.component.css']
})
export class AssignNewOrderDialogComponent implements OnInit {

  @ViewChild('stepper') stepper!: MatStepper;

  constructor(private customerOrderAssignationService: CustomerOrderAssignationService,
    public dialogRef: MatDialogRef<AssignNewOrderDialogComponent>,
    private appService: AppService,
    private employeeService: EmployeeService,
    private constantService: ConstantService,
    private activeDirectoryGroupService: ActiveDirectoryGroupService,
    private orderService: CustomerOrderService
  ) { }

  foundOrder: number | undefined;
  isLoading = false;
  complexity: number = 3;
  isInsertionEmployee = false;
  currentEmployee: Employee | undefined;

  ngOnInit() {
    this.isLoading = true;

    this.employeeService.getCurrentEmployee().subscribe(employee => {
      this.currentEmployee = employee;
      this.isInsertionEmployee = this.activeDirectoryGroupService.isEmployeeInGroupList(this.currentEmployee, [this.constantService.getActiveDirectoryGroupInsertions()])

      if (this.isInsertionEmployee) {
        this.orderService.assignNewCustomerOrderToOrderForInsertions().subscribe(response => {
          if (response) {
            this.isLoading = false;
            this.foundOrder = response.id;
          }
          else {
            this.nextStep();
            this.customerOrderAssignationService.getNextPriorityOrderForFond().subscribe(priorityFond => {
              if (priorityFond) {
                this.isLoading = false;
                this.foundOrder = priorityFond;
              } else {
                this.nextStep();
              }
            })
          }
        });
      } else {
        this.nextStep();
        this.customerOrderAssignationService.getNextPriorityOrderForFond().subscribe(priorityFond => {
          if (priorityFond) {
            this.isLoading = false;
            this.foundOrder = priorityFond;
          } else {
            this.nextStep();
          }
        })
      }
    })
  }

  onConfirm(): void {
    this.appService.openRoute({ ctrlKey: true }, 'order/' + this.foundOrder, null);
    this.dialogRef.close(this.foundOrder);
  }

  onClose(): void {
    this.dialogRef.close(false);
  }

  setComplexity(complexity: number) {
    this.complexity = complexity;
    this.nextStep();
    this.isLoading = true;
    this.customerOrderAssignationService.getNextPriorityOrderForCommon(this.complexity).subscribe(priorityCommon => {
      if (priorityCommon) {
        this.isLoading = false;
        this.foundOrder = priorityCommon;
      } else {
        this.nextStep();
        this.isLoading = true;
        this.customerOrderAssignationService.getFondTypeToUse(this.complexity).subscribe(fond => {
          if (fond)
            if (fond.type == 'FOND') {
              this.customerOrderAssignationService.getNextOrderForFond(this.complexity).subscribe(fond => {
                if (fond) {
                  this.isLoading = false;
                  this.foundOrder = fond;
                }
              })
            }
          if (fond.type == 'COMMON') {
            this.nextStep();
            this.isLoading = true;
            this.customerOrderAssignationService.getNextOrderForCommon(this.complexity).subscribe(common => {
              this.isLoading = false;
              if (common) {
                this.foundOrder = common;
              }
            })
          }
        })
      }
    })
  }

  onStepChange(event: StepperSelectionEvent) {
    const preventManualStepChange = true;

    if (preventManualStepChange) {
      setTimeout(() => {
        this.stepper.selectedIndex = event.previouslySelectedIndex;
      });
    }
  }

  nextStep() {
    this.stepper.next();
  }

}
