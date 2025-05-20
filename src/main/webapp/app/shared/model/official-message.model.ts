import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface IOfficialMessage {
  id?: number;
  title?: string | null;
  body?: string;
  timePosted?: dayjs.Dayjs;
  user?: IUser;
}

export const defaultValue: Readonly<IOfficialMessage> = {};
