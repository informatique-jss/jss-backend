import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { JOURNAL_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { formatDate } from '../../../../libs/FormatHelper';
import { AppService } from '../../../../services/app.service';
import { ConstantService } from '../../../miscellaneous/services/constant.service';
import { Journal } from '../../model/Journal';
import { JournalService } from '../../services/journal.service';

@Component({
  selector: 'app-add-journal',
  templateUrl: './add-journal.component.html',
  styleUrls: ['./add-journal.component.css']
})
export class AddJournalComponent implements OnInit {

  constructor(
    private activatedRoute: ActivatedRoute,
    private appService: AppService,
    private journalService: JournalService,
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
  ) { }

  idJournal: number | undefined;
  journal: Journal = {} as Journal;
  journalForm = this.formBuilder.group({});
  JOURNAL_ENTITY_TYPE = JOURNAL_ENTITY_TYPE;
  attachmentTypeJournal = this.constantService.getAttachmentTypeJournal();

  ngOnInit() {
    this.idJournal = this.activatedRoute.snapshot.params.id;
    if (this.idJournal)
      this.journalService.getJournal(this.idJournal).subscribe(response => {
        this.journal = response;
        this.appService.changeHeaderTitle("Journal du " + formatDate(this.journal.journalDate))
      });
  }

  saveJournal() {
    if (this.journalForm.valid)
      this.journalService.addOrUpdateJournal(this.journal).subscribe(response => this.appService.openRoute(null, "/journal", null));
    else
      this.appService.displaySnackBar("L'ensemble des champs sont obligatoires !", true, 15);
  }

  updateAttachments(attachments: Attachment[]) {
    if (attachments && this.journal) {
      this.journal.attachments = attachments;
    }
  }
}
