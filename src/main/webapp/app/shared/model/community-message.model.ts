import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { MessageType } from 'app/shared/model/enumerations/message-type.model';

export interface ICommunityMessage {
  id?: number;
  content?: string;
  time_posted?: dayjs.Dayjs;
  type?: keyof typeof MessageType;
  parentId?: number | null;
  approved?: boolean;
  user?: IUser;
}

export const defaultValue: Readonly<ICommunityMessage> = {
  approved: false,
};
