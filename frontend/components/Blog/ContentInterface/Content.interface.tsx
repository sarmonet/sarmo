
export interface IContentBlock {
	id: number;
	contentId: number;
	title: string;
	description: string;
	mainImage: string;
	publicationDate: string;
	author: {
		id: number;
		firstName: string;
		lastName: string;
		profilePictureUrl: string;
	}
}

export interface IContent {
	id?: string;	
	mainImage: string;
	title: string;
	description: string;
	content: IContentItem[];
}

export interface IContentItem {
	type: string;
	title: string;
	value: string;
}