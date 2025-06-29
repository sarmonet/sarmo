export interface IChat {
	id: number;
	chatImageUrl: string;
	name: string;
	creatorId: number;
	content: [] | null;
	userIds: (number | string)[] | null;
}