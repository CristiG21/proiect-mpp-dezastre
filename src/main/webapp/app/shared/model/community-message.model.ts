import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface ICommunityMessage {
  id?: number;
  content?: string;
  time_posted?: dayjs.Dayjs;
  parentId?: number | null;
  approved?: boolean;
  user?: IUser;
  parent?: ICommunityMessage | null;
}

export const defaultValue: Readonly<ICommunityMessage> = {
  approved: false,
};
