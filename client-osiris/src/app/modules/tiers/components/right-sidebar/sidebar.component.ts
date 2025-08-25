import { Component, inject, TemplateRef } from '@angular/core';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { SimplebarAngularModule } from 'simplebar-angular';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { ResponsableSelectionComponent } from "../responsable-selection/responsable-selection.component";
import { TasksWidgetComponent } from "../tasks-widget/tasks-widget.component";

@Component({
  selector: 'app-sidebar',
  imports: [
    SHARED_IMPORTS,
    NgIcon,
    SimplebarAngularModule,
    ResponsableSelectionComponent,
    TasksWidgetComponent,
  ],
  standalone: true,
  templateUrl: './sidebar.component.html',
  styles: ``
})
export class SidebarComponent {
  private offcanvasService = inject(NgbOffcanvas);

  open(content: TemplateRef<any>) {
    this.offcanvasService.open(content, { panelClass: 'asidebar border-start overflow-hidden', position: 'end' })
  }
}
