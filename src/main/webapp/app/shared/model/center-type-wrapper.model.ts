import { ICenter } from 'app/shared/model/center.model';
import { CenterType } from 'app/shared/model/enumerations/center-type.model';

export interface ICenterTypeWrapper {
  id?: number;
  type?: keyof typeof CenterType | null;
  center?: ICenter | null;
}

export const defaultValue: Readonly<ICenterTypeWrapper> = {};
