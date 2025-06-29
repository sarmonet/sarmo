
export interface IDynamicFields {
  [key: string]: unknown;
}

export interface IMongo {
  fields: IDynamicFields; 
}