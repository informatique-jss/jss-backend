import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { AppService } from 'src/app/services/app.service';
import { RestUserPreferenceService } from '../../../../services/rest.user.preference.service';
import { KanbanView } from '../../model/KanbanView';

@Component({
  selector: 'save-current-view-dialog',
  templateUrl: './save-current-view-dialog.component.html',
  styleUrls: ['./save-current-view-dialog.component.css']
})
export class SaveCurrentViewDialog<T, U extends IWorkflowElement<T>> implements OnInit {

  constructor(private restUserPreferenceService: RestUserPreferenceService,
    public dialogRef: MatDialogRef<SaveCurrentViewDialog<T, U>>,
    private appService: AppService,
    private formBuilder: FormBuilder
  ) { }

  @Input() kanbanViewToSave: KanbanView<T, U> | undefined;

  @Output() viewLabelEmitter: EventEmitter<string> = new EventEmitter<string>();
  viewLabel: string | undefined;

  isLoading = false;

  kanbanForm = this.formBuilder.group({});

  ngOnInit() {
  }

  onConfirm(): void {
    // if (this.viewLabel) {
    //   if (this.componentToSaveViewCode) {
    //     this.restUserPreferenceService.getUserPreferenceValue(this.componentToSaveViewCode + "_" + CUSTOM_USER_PREFERENCE).subscribe(jsonValue => {
    //       let kanbanViews: KanbanView<T, U>[] = JSON.parse(jsonValue);
    //       if (this.kanbanViewToSave) {
    //         this.kanbanViewToSave.label = this.viewLabel!;
    //         kanbanViews.push(this.kanbanViewToSave);
    //         this.restUserPreferenceService.setUserPreference(kanbanViews, this.componentToSaveViewCode + "_" + CUSTOM_USER_PREFERENCE).subscribe();
    //       }
    //     });
    //   } 
    // } else {
    //   this.appService.displaySnackBar("Veuillez renseigner un nom à la vue que vous souhaitez enregister", true, 3000);
    // }
    this.dialogRef.close(this.viewLabel);
  }

  onClose(): void {
    this.dialogRef.close(false);
  }
}
