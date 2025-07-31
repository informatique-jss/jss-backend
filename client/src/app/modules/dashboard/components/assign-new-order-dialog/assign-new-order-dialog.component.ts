import { StepperSelectionEvent } from '@angular/cdk/stepper';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { CustomerOrderAssignationService } from 'src/app/modules/quotation/services/customer.assignation.service';
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
    private appService: AppService
  ) { }

  foundOrder: number | undefined;
  isLoading = false;
  complexity: number = 3;

  ngOnInit() {
    this.isLoading = true;
    this.customerOrderAssignationService.getNextPriorityOrderForFond().subscribe(priorityFond => {
      if (priorityFond) {
        this.isLoading = false;
        this.foundOrder = priorityFond;
      } else {
        this.nextStep();
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
