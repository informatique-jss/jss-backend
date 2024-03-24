import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { Service } from '../../model/Service';
import { ServiceService } from '../../services/service.service';
import { SelectServiceTypeDialogComponent } from '../select-service-type-dialog/select-service-type-dialog.component';

@Component({
  selector: 'app-select-service-dialog',
  templateUrl: './select-service-dialog.component.html',
  styleUrls: ['./select-service-dialog.component.css']
})
export class SelectServiceDialogComponent implements OnInit {

  assoAffaireOrder: AssoAffaireOrder | undefined;
  selectedService: Service | undefined;
  currentService: Service | undefined;

  constructor(private formBuilder: FormBuilder,
    private dialog: MatDialog,
    private serviceService: ServiceService,
    public dialogRef: MatDialogRef<SelectServiceTypeDialogComponent>
  ) { }

  serviceTypeForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  getFormStatus(): boolean {
    return this.serviceTypeForm.valid;
  }

  selectService(service: Service) {
    this.selectedService = service;
  }

  validateService() {
    if (this.selectedService)
      this.dialogRef.close(this.selectedService);
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

}
