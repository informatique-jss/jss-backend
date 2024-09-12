/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

<<<<<<<< HEAD:client/src/app/modules/miscellaneous/components/forms/chips-principal-accounting-account/chips-principal-accounting-account.component.spec.ts
import { ChipsPrincipalAccountingAccountComponent } from './chips-principal-accounting-account.component';

describe('ChipsPrincipalAccountingAccountComponent', () => {
  let component: ChipsPrincipalAccountingAccountComponent;
  let fixture: ComponentFixture<ChipsPrincipalAccountingAccountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChipsPrincipalAccountingAccountComponent ]
========
import { MultipleUploadComponent } from './multiple-upload.component';

describe('MultipleUploadComponent', () => {
  let component: MultipleUploadComponent;
  let fixture: ComponentFixture<MultipleUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MultipleUploadComponent ]
>>>>>>>> develop:client/src/app/modules/miscellaneous/components/multiple-upload/multiple-upload.component.spec.ts
    })
    .compileComponents();
  }));

  beforeEach(() => {
<<<<<<<< HEAD:client/src/app/modules/miscellaneous/components/forms/chips-principal-accounting-account/chips-principal-accounting-account.component.spec.ts
    fixture = TestBed.createComponent(ChipsPrincipalAccountingAccountComponent);
========
    fixture = TestBed.createComponent(MultipleUploadComponent);
>>>>>>>> develop:client/src/app/modules/miscellaneous/components/multiple-upload/multiple-upload.component.spec.ts
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
