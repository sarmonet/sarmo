import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { delComment, getComment, postComment } from '@/services/Commentaries'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from 'next/legacy/image'
import Link from 'next/link'
import React, { useEffect, useState } from 'react'
import { BsReply } from 'react-icons/bs'
import { FaRegTrashAlt } from 'react-icons/fa'
import { ICommentaries } from './Commentaries.interface'
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
} from './ListingComentaries.styled'

export const ListingCommentaries = ({ listingId }: { listingId: number }) => {
	const { user } = useCatalog()
	const [commentInputText, setCommentInputText] = useState<string>('')
	const [comments, setComments] = useState<ICommentaries[]>([])
	const [replyTo, setReplyTo] = useState<number | null>(null)
	const { t } = useTranslation('common')
	useEffect(() => {
		const fetchCommentaries = async () => {
			try {
				const data = await getComment({ listingId })
				if (data) {
					setComments(data)
				}
			} catch (error) {
				console.error('Ошибка загрузки комментариев:', error)
			}
		}

		fetchCommentaries()
	}, [listingId])

	const handlePostComment = async () => {
		if (!user) {
			console.log(
				'Только зарегистрированные пользователи могут комментировать.'
			)
			return
		}

		if (commentInputText.trim()) {
			try {
				await postComment({
					listingId: listingId,
					content: commentInputText,
					parentCommentId: replyTo || undefined,
				})
				setCommentInputText('')
				setReplyTo(null)

				const data = await getComment({ listingId })
				if (data) {
					setComments(data)
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
			const updatedComments = await getComment({ listingId })
			if (updatedComments) {
				setComments(updatedComments)
			}
		} catch (error) {
			console.error('Ошибка удаления комментария:', error)
		}
	}

	const renderComment = (comment: ICommentaries) => {
		if (!comment.id) return null

		const replies = comments.filter(reply => reply.parentId === comment.id)

		return (
			<React.Fragment key={comment.id}>
				<CommentariesBlock>
					<div style={{ display: 'flex', gap: '16px' }}>
						<Image
							src='/images/user/altUser.png'
							alt='avatar'
							width='60'
							height='60'
						/>
						<CommentariesBody>
							<div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
								<h3>{comment.author?.firstName || 'Аноним'}</h3>
								<span>{new Date(comment.createdAt).toLocaleString()}</span>
							</div>

							<p>{comment.content}</p>

							<CommentariesReplyButton>
								{user && (
									<CommentariesReply
										onClick={() => handleReply(comment.id as number)}
									>
										<BsReply size={18} /> Ответить
									</CommentariesReply>
								)}
								{user?.id === comment.author?.id && (
									<CommentariesDelete
										onClick={() => handleDeleteComment(comment.id as number)}
									>
										<FaRegTrashAlt size={18} fill='red' />
									</CommentariesDelete>
								)}
							</CommentariesReplyButton>

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
					</div>

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
				<h2>{t('listing.commentaries')}</h2>
				<span>{comments.length}</span>
			</CommentariesHeader>

			{user ? (
				<CommentariesInput>
					<textarea
						placeholder={t('listing.commentariesPlaceholder')}
						value={commentInputText}
						onChange={e => setCommentInputText(e.target.value)}
						onKeyDown={e => e.key === 'Enter' && handlePostComment()}
					/>
					<button onClick={handlePostComment}>{t('contentBlog.submit')}</button>
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
					[ {t('listing.warningComment')}]
				</Link>
			)}

			<CommentariesBlocks>
				{comments.filter(comment => !comment.parentId).map(renderComment)}
			</CommentariesBlocks>
		</CommentariesWrapper>
	)
}
