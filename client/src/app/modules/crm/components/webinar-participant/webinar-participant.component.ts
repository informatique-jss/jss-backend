import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { Webinar } from '../../model/Webinar';
import { WebinarParticipant } from '../../model/WebinarParticipant';
import { WebinarParticipantService } from '../../services/webinar.participant.service';

@Component({
  selector: 'webinar-participant',
  templateUrl: './webinar-participant.component.html',
  styleUrls: ['./webinar-participant.component.css'],
})
export class WebinarParticipantComponent implements OnInit {

  displayedColumnsWebinarParticipants: SortTableColumn<WebinarParticipant>[] = [];
  tableActionWebinarParticipant: SortTableAction<WebinarParticipant>[] = [];
  webinarParticipants: WebinarParticipant[] = [];
  selectedWebinar: Webinar | undefined;
  bookmark: Webinar | undefined;

  constructor(private formBuilder: FormBuilder,
    public confirmationDialog: MatDialog,
    private webinarParticipantService: WebinarParticipantService,
    private userPreferenceService: UserPreferenceService
  ) { }

  ngOnInit() {
    this.displayedColumnsWebinarParticipants = [];

    this.displayedColumnsWebinarParticipants.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<WebinarParticipant>);
    this.displayedColumnsWebinarParticipants.push({ id: "name", fieldName: "firstname " + "lastname", label: "Prénom & nom", valueFonction: (element: WebinarParticipant, column: SortTableColumn<WebinarParticipant>) => { return (element.firstname + " " + element.lastname) } } as SortTableColumn<WebinarParticipant>);
    this.displayedColumnsWebinarParticipants.push({ id: "mail", fieldName: "mail.mail", label: "Mail" } as SortTableColumn<WebinarParticipant>);
    this.displayedColumnsWebinarParticipants.push({ id: "phoneNumber", fieldName: "phoneNumber", label: "Numéro de téléphone" } as SortTableColumn<WebinarParticipant>);
    this.displayedColumnsWebinarParticipants.push({ id: "comment", fieldName: "comment", label: "Commentaire" } as SortTableColumn<WebinarParticipant>);

    this.tableActionWebinarParticipant.push({
      actionIcon: "delete", actionName: "Supprimer le participant", actionClick: (column: SortTableAction<WebinarParticipant>, element: WebinarParticipant, event: any) => {
        if (element) {
          const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
            maxWidth: "400px",
            data: {
              title: "Supprimer le participant au webinaire",
              content: "Êtes-vous sûr de vouloir continuer ?",
              closeActionText: "Annuler",
              validationActionText: "Supprimer"
            }
          });

          dialogRef.afterClosed().subscribe(dialogResult => {
            if (dialogResult)
              this.webinarParticipantService.deleteWebinarParticipant(element).subscribe(response => {
                if (response)
                  this.searchWebinarParticipants();
              });
          });
        }
      }, display: true,
    } as SortTableAction<WebinarParticipant>);
    this.bookmark = this.userPreferenceService.getUserSearchBookmark("webinar") as Webinar;
    if (this.bookmark) {
      this.selectedWebinar = this.bookmark;
      this.searchWebinarParticipants();
    }
  }

  webinarParticipantForm = this.formBuilder.group({
  });

  searchWebinarParticipants() {
    if (this.selectedWebinar) {
      this.userPreferenceService.setUserSearchBookmark(this.selectedWebinar, "webinar");
      this.webinarParticipantService.getWebinarParticipants(this.selectedWebinar).subscribe(response => {
        if (response)
          this.webinarParticipants = response;
      });
    }
  }
}
