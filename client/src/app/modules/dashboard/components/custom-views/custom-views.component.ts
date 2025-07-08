import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { AppService } from 'src/app/services/app.service';
import { RestUserPreferenceService } from '../../../../services/rest.user.preference.service';
import { KanbanView } from '../../model/KanbanView';
import { CUSTOM_USER_PREFERENCE } from '../../model/UserPreference';
import { SaveCurrentViewDialog } from '../save-current-view-dialog/save-current-view-dialog.component';

@Component({
  selector: 'custom-views',
  templateUrl: './custom-views.component.html',
  styleUrls: ['./custom-views.component.css']
})
export class CustomViewsComponent<T, U extends IWorkflowElement<T>> implements OnInit {

  kanbanViews: KanbanView<T, U>[] = [];

  @Input() componentToSaveViewCode: string | undefined;

  @Input() currentView: KanbanView<T, U> | undefined;

  @Output() selectedViewEmitter: EventEmitter<KanbanView<T, U>> = new EventEmitter;

  constructor(private restUserPreferenceService: RestUserPreferenceService,
    public saveCurrentViewDialog: MatDialog,
    public appService: AppService,
  ) { }

  ngOnInit() {
    if (!this.componentToSaveViewCode) {
      this.appService.displaySnackBar("Une erreur s'est produite, merci de contacter l'administrateur", true, 3000);
      return;
    }
    if (!this.currentView) {
      this.appService.displaySnackBar("Une erreur s'est produite, merci de contacter l'administrateur", true, 3000);
      return;
    }
    this.updateKanbanViewsList();
  }

  updateKanbanViewsList() {
    this.restUserPreferenceService.getUserPreferenceValue(this.componentToSaveViewCode + "_" + CUSTOM_USER_PREFERENCE).subscribe(customUserPreferenceJson => {
      let kanbanViews: KanbanView<T, U>[] = JSON.parse(customUserPreferenceJson);
      if (kanbanViews) {
        this.kanbanViews = [];
        for (let kanbanView of kanbanViews) {
          this.kanbanViews.push(kanbanView);
        }
      }
    });
  }

  saveCurrentView() {
    const dialogRef = this.saveCurrentViewDialog.open(SaveCurrentViewDialog, {
      width: "50%"
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        if (this.componentToSaveViewCode) {
          this.restUserPreferenceService.getUserPreferenceValue(this.componentToSaveViewCode + "_" + CUSTOM_USER_PREFERENCE).subscribe(jsonValue => {
            let kanbanViews: KanbanView<T, U>[] = JSON.parse(jsonValue);
            if (!kanbanViews)
              kanbanViews = [];
            if (this.currentView) {
              this.currentView.label = dialogResult!;
              kanbanViews.push(this.currentView);
              this.restUserPreferenceService.setUserPreference(JSON.stringify(kanbanViews), this.componentToSaveViewCode + "_" + CUSTOM_USER_PREFERENCE).subscribe(res => {
                this.updateKanbanViewsList();
              });
            }
          });
        }
      }
    });
  }

  deleteView(indexViewToDelete: number, event: Event) {
    event.preventDefault();
    if (indexViewToDelete) {
      if (this.componentToSaveViewCode) {
        this.restUserPreferenceService.getUserPreferenceValue(this.componentToSaveViewCode + "_" + CUSTOM_USER_PREFERENCE).subscribe(jsonValue => {
          let kanbanViews: KanbanView<T, U>[] = JSON.parse(jsonValue);
          kanbanViews.splice(indexViewToDelete);
          this.restUserPreferenceService.setUserPreference(JSON.stringify(kanbanViews), this.componentToSaveViewCode + "_" + CUSTOM_USER_PREFERENCE).subscribe(res => {
            this.updateKanbanViewsList();
          });
        });
      }
    }
  }

  selectView(selectedView: KanbanView<T, U>) {
    this.currentView = selectedView;
    this.selectedViewEmitter.emit(this.currentView);
  }
}
