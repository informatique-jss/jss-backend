import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { Service } from '../../model/Service';
import { ServiceService } from '../../services/service.service';

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
    private constantService: ConstantService,
    public dialogRef: MatDialogRef<SelectServiceDialogComponent>
  ) { }

  serviceTypeForm = this.formBuilder.group({});

  getServiceLabel(service: Service) {
    return this.serviceService.getServiceLabel(service, false, this.constantService.getServiceTypeOther());
  }

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
