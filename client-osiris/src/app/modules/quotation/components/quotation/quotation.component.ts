import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { QuotationDto } from '../../model/QuotationDto';
import { IQuotationCommentService } from '../../services/iquotation-comment.service';
import { QuotationService } from '../../services/quotation.service';

@Component({
  selector: 'quotation',
  templateUrl: './quotation.component.html',
  styleUrls: ['./quotation.component.css']
})
export class QuotationComponent implements OnInit {

  quotation: QuotationDto | undefined;
  idQuotation: number | undefined;

  constructor(
    private quotationService: QuotationService,
    private iQuotationCommentService: IQuotationCommentService,
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.idQuotation = this.activatedRoute.snapshot.params['id'] != "null" ? this.activatedRoute.snapshot.params['id'] : null;
    if (this.idQuotation)
      this.quotationService.getQuotation(this.idQuotation).subscribe(response => {
        if (response) {
          this.quotation = response;
        }
      });
  }

  openChatForQuotation() {
    if (this.quotation)
      this.iQuotationCommentService.openChatForIQuotation(this.quotation.id);
  }



}
