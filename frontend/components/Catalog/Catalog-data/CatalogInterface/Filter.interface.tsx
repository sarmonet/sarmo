
export interface IFilter{
	onFilterChange: (filters: unknown) => void;
	filters: number | string | boolean | [];
}

export interface IField {
  name: string;
  type: "Boolean" | "Integer" | "Double" | "String" | "List" | "File"; 
  filterable: boolean;
  required: boolean;
}

export interface IFields {
  categoryId: number;
  fields: IField[];
}