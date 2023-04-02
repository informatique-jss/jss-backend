import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

declare var $: any;

@Component({
  selector: 'import-html-announcement-dialog',
  templateUrl: './import-html-announcement-dialog.component.html',
  styleUrls: ['./import-html-announcement-dialog.component.css']
})
export class ImportHtmlAnnouncementDialogComponent implements OnInit {

  constructor(private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<ImportHtmlAnnouncementDialogComponent>
  ) { }

  html: string = "";

  ngOnInit() {
  }

  importHtmlForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.importHtmlForm.valid;
  }

  importHtml() {
    let value = this.getIframeDocument().querySelector('.CodeMirror').CodeMirror.getValue();
    if (this.getFormStatus() && value) {
      this.dialogRef.close(value);
    }
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

  onLoad() {
    // Hide html code
    var cm = (this.getIframeDocument().querySelector('.CodeMirror') as any).CodeMirror;
    $(cm.getWrapperElement()).hide();

    // Hide toolbars
    this.getIframeDocument().querySelector('.fr-toolbar').hidden = true;
    this.getIframeDocument().querySelector('.fr-second-toolbar').hidden = true;
  }

  getIframeDocument() {
    var iframe = document.getElementById('htmlConverter') as any;
    if (iframe)
      return iframe.contentDocument || iframe.contentWindow.document;
  }

}
