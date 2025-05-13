import { ICenter } from 'app/shared/model/center.model';

export interface IPhotoURL {
  id?: number;
  url?: string | null;
  center?: ICenter | null;
}

export const defaultValue: Readonly<IPhotoURL> = {};
