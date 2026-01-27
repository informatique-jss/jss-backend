
export interface CustomerOrderCommentDto {
  id: number;
  createdDateTime: Date;
  employee: number;
  currentCustomer: number;
  comment: string;
  customerOrder: number;
  isRead: boolean;
  isToDisplayToCustomer: boolean;
  isCurrentUserInGroup: boolean;
  isFromTchat: boolean;
  isReadByCustomer: boolean;
}
