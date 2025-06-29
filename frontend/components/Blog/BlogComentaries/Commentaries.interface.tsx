export interface ICommentaries {
	id: number | null
	userId: number | null
	articleId: number | null
	text: string
	parentCommentId?: number | null
	creationDate: string
	createdAt: string
	author: {
		id: number
		firstName: string
		lastName: string
		profilePictureUrl: string
	}
}
