
/**
 * The datas displayed in dasboard to follow teams
 */
export class ProvisionBoardDisplayed {
  employee!: string;

  nbProvisionNew!: number;
	nbProvisionInProgress!: number;
	nbProvisionWaiting!: number;
	nbProvisionWaitingGreffe!: number;
	nbProvisionWaitingAuthority!: number;
	nbProvisionConfrerePublished!: number;
	nbProvisionValidateGreffe!: number;
	nbProvisionRefusedGreffe!: number;
	nbProvisionPublished!: number;
	nbProvisionDone!: number;

  ProvisionBoardDisplayed() {

    this.nbProvisionNew = 0;
    this.nbProvisionInProgress = 0;
    this.nbProvisionWaiting = 0;
    this.nbProvisionWaitingGreffe = 0;
    this.nbProvisionWaitingAuthority = 0;
    this.nbProvisionConfrerePublished = 0;
    this.nbProvisionValidateGreffe = 0;
    this.nbProvisionRefusedGreffe = 0;
    this.nbProvisionPublished = 0;
    this.nbProvisionDone = 0;
  }

  initNbProvision(  nbProvisionNew: number, nbProvisionInProgress: number,
                    nbProvisionWaiting: number, nbProvisionWaitingGreffe: number,
                    nbProvisionWaitingAuthority: number, nbProvisionConfrerePublished: number,
                    nbProvisionValidateGreffe: number, nbProvisionRefusedGreffe: number,
                    nbProvisionPublished: number, nbProvisionDone: number
                  ) {

    this.nbProvisionNew = nbProvisionNew;
    this.nbProvisionInProgress = nbProvisionInProgress;
    this.nbProvisionWaiting = nbProvisionWaiting;
    this.nbProvisionWaitingGreffe = nbProvisionWaitingGreffe;
    this.nbProvisionWaitingAuthority = nbProvisionWaitingAuthority;
    this.nbProvisionConfrerePublished = nbProvisionConfrerePublished;
    this.nbProvisionValidateGreffe = nbProvisionValidateGreffe;
    this.nbProvisionRefusedGreffe = nbProvisionRefusedGreffe;
    this.nbProvisionPublished = nbProvisionPublished;
    this.nbProvisionDone = nbProvisionDone;
  }

}


