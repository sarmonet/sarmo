
export interface ICatalog {
	id: number;
	imageUrl: string;
	name: string,
}

export interface ICatalogField {
	name: string;
  type: string;
  filterable: boolean;
  required: boolean;
}
export interface ICatalogWithFields {
	id: number;
	categoryName: string,
	fields: ICatalogField[],
}

export interface ICatalogSub{
	id: number;
	name: string,
	category: ICatalog,
}