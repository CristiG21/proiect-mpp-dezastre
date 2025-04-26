export interface ICenter {
  id?: number;
  name?: string | null;
  longitude?: number | null;
  latitude?: number | null;
  status?: boolean | null;
}

export const defaultValue: Readonly<ICenter> = {
  status: false,
};
