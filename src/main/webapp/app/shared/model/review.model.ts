import { ICenter } from 'app/shared/model/center.model';
import { IUser } from 'app/shared/model/user.model';

export interface IReview {
  id?: number;
  stars?: number;
  text?: string | null;
  center?: ICenter | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IReview> = {};
