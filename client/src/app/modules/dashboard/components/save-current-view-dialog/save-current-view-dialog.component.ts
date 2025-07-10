import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
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
    private formBuilder: FormBuilder
  ) { }

  @Input() kanbanViewToSave: KanbanView<T, U> | undefined;

  @Output() viewLabelEmitter: EventEmitter<string> = new EventEmitter<string>();
  viewLabel: string | undefined;

  kanbanForm = this.formBuilder.group({});

  ngOnInit() {
  }

  onConfirm(): void {
    this.dialogRef.close(this.viewLabel);
  }

  onClose(): void {
    this.dialogRef.close(false);
  }
}
