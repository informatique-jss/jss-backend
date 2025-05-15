import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { UploadAttachmentService } from 'src/app/modules/miscellaneous/services/upload.attachment.service';
import { AppService } from '../../../../../../../client-jss/src/app/services/app.service';
import { Candidacy } from '../../model/Candidacy';
import { CandidacyService } from '../../services/candidacy.service';

@Component({
  selector: 'candidacy',
  templateUrl: './candidacy.component.html',
  styleUrls: ['./candidacy.component.css']
})
export class CandidacyComponent implements OnInit {
  //TODO Add notification when new candidacy is received + Use Api ?
  displayedColumnsCandidacies: SortTableColumn<Candidacy>[] = [];
  tableActionWebinarParticipant: SortTableAction<Candidacy>[] = [];
  candidacies: Candidacy[] = [];
  tableActions: SortTableAction<Candidacy>[] = [] as Array<SortTableAction<Candidacy>>;
  attachmentCvCandidacies: Attachment[] = [];

  constructor(private formBuilder: FormBuilder,
    private candidacyService: CandidacyService,
    private uploadAttachmentService: UploadAttachmentService,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.displayedColumnsCandidacies = [];

    this.displayedColumnsCandidacies.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<Candidacy>);
    this.displayedColumnsCandidacies.push({ id: "searchedJob", fieldName: "searchedJob", label: "Poste recherché" } as SortTableColumn<Candidacy>);
    this.displayedColumnsCandidacies.push({ id: "mail", fieldName: "mail", label: "Mail" } as SortTableColumn<Candidacy>);

    this.tableActions.push({
      actionIcon: "visibility",
      actionName: "Voir le CV",
      actionClick: (column: SortTableAction<Candidacy>, candidacy: Candidacy, event: any): void => {
        const attachment = candidacy.attachments[0];
        if (attachment) {
          this.uploadAttachmentService.previewAttachment(attachment);
        } else {
          this.appService.displayToast('Aucun fichier attaché à cette candidature', true, "Erreur de téléchargement", 3000);
        }
      },
      display: true
    } as SortTableAction<Candidacy>);


    this.candidacyService.getCandidacies().subscribe(response => {
      if (response)
        this.candidacies = response;
    });
  }

  candidaciesForm = this.formBuilder.group({
  });
}
