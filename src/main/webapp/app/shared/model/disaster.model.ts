import { DisasterType } from 'app/shared/model/enumerations/disaster-type.model';

export interface IDisaster {
  id?: number;
  name?: string | null;
  longitude?: number | null;
  latitude?: number | null;
  radius?: number | null;
  type?: keyof typeof DisasterType | null;
}

export const defaultValue: Readonly<IDisaster> = {};
