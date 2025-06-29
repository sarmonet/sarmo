import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { delComment, getComment, postComment } from '@/services/getContent'
import { colors } from '@/utils'
import Image from 'next/legacy/image'
import Link from 'next/link'
import React, { useEffect, useState } from 'react'
import { BsReply } from 'react-icons/bs'
import { FaRegTrashAlt } from 'react-icons/fa'

import {
	CommentariesBlock,
	CommentariesBlocks,
	CommentariesBody,
	CommentariesDelete,
	CommentariesHeader,
	CommentariesInput,
	CommentariesReply,
	CommentariesReplyButton,
	CommentariesWrapper,
	ReplyInput,
} from './BlogComentaries.styled'
import { ICommentaries } from './Commentaries.interface'

export const BlogCommentaries = ({ articleId }: { articleId: number }) => {
	const { user } = useCatalog()
	const [commentInputText, setCommentInputText] = useState<string>('')
	const [comments, setComments] = useState<ICommentaries[]>([])
	const [replyTo, setReplyTo] = useState<number | null>(null)
	useEffect(() => {
		const fetchCommentaries = async () => {
			try {
				const data = await getComment({ articleId })

				if (Array.isArray(data)) {
					setComments(data)
				} else if (data) {
					setComments([data])
				} else {
					setComments([])
				}
			} catch (error) {
				console.error('Ошибка загрузки комментариев:', error)
				setComments([])
			}
		}

		fetchCommentaries()
	}, [articleId])

	const handlePostComment = async () => {
		if (commentInputText.trim()) {
			try {
				await postComment({
					articleId: articleId,
					text: commentInputText,
					parentCommentId: replyTo || undefined,
				})
				setCommentInputText('')
				setReplyTo(null)

				const data = await getComment({ articleId })
				if (Array.isArray(data)) {
					setComments(data)
				} else if (data) {
					setComments([data])
				} else {
					setComments([])
				}
			} catch (error) {
				console.error('Ошибка отправки комментария:', error)
			}
		} else {
		}
	}

	const handleReply = (commentId: number) => {
		if (!user) return
		setReplyTo(commentId)
	}

	const handleDeleteComment = async (commentId: number) => {
		try {
			await delComment({ commentId })
			const updatedComments = await getComment({ articleId })

			if (Array.isArray(updatedComments)) {
				setComments(updatedComments)
			} else if (updatedComments) {
				setComments([updatedComments])
			} else {
				setComments([])
			}
		} catch (error) {
			console.error('Ошибка удаления комментария:', error)
		}
	}

	const renderComment = (comment: ICommentaries) => {
		if (!comment.id) {
			return null
		}
		const replies = comments.filter(
			reply => reply.parentCommentId === comment.id
		)
		return (
			<React.Fragment key={comment.id}>
				<CommentariesBlock>
					<Image
						src={comment.author.profilePictureUrl || '/images/user/altUser.png'}
						width={60}
						height={60}
						alt='user avatar'
						className='img'
					/>
					<CommentariesBody>
						<h3>
							{comment.author.firstName} {comment.author.lastName}
						</h3>
						<span>{new Date(comment.creationDate).toLocaleDateString()}</span>
						<div
							style={{
								display: 'flex',
								flexDirection: 'column',
								alignItems: 'flex-start',
							}}
						>
							<p>{comment.text}</p>
							<CommentariesReplyButton>
								{(user?.id === comment.author?.id) !== null && (
									<CommentariesDelete
										onClick={() => handleDeleteComment(comment.id as number)}
									>
										<FaRegTrashAlt size={18} fill='red' />
									</CommentariesDelete>
								)}
								{user && comment.id !== null && (
									<CommentariesReply
										onClick={() => handleReply(comment.id as number)}
									>
										<BsReply size={18} /> Ответить
									</CommentariesReply>
								)}
							</CommentariesReplyButton>
						</div>

						{replyTo === comment.id && (
							<ReplyInput>
								<div onClick={() => setReplyTo(null)}>X</div>
								<input
									type='text'
									placeholder='Ваш ответ'
									value={commentInputText}
									onChange={e => setCommentInputText(e.target.value)}
								/>
								<button onClick={handlePostComment}>Отправить</button>
							</ReplyInput>
						)}
					</CommentariesBody>
					{replies.length > 0 && (
						<div style={{ marginLeft: 20 }}>{replies.map(renderComment)}</div>
					)}
				</CommentariesBlock>
			</React.Fragment>
		)
	}

	return (
		<CommentariesWrapper>
			<CommentariesHeader>
				<h2>Комментарии</h2>
				<span>{comments.length}</span>
			</CommentariesHeader>

			{user ? (
				<CommentariesInput>
					<textarea
						placeholder='Ваш комментарий'
						value={commentInputText}
						onChange={e => setCommentInputText(e.target.value)}
						onKeyDown={e => e.key === 'Enter' && handlePostComment()}
					/>
					<button onClick={handlePostComment}>Отправить</button>
				</CommentariesInput>
			) : (
				<Link
					href='/login'
					style={{
						marginTop: '16px',
						color: colors.btnMainColor,
						fontStyle: 'italic',
						textDecoration: 'none',
					}}
				>
					[ Чтобы оставить комментарий или рейтинг, вы должны быть авторизованы.
					]
				</Link>
			)}

			<CommentariesBlocks>
				{comments
					.filter(comment => !comment.parentCommentId)
					.map(renderComment)}
			</CommentariesBlocks>
		</CommentariesWrapper>
	)
}
