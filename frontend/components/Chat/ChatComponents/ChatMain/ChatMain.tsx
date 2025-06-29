// src/components/Chat/ChatMain/ChatMain.tsx
'use client'

import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import {
	Attachment,
	ChatMessageWebSocketDTO,
	connectWebSocket,
	deleteChat,
	deleteMessage,
	disconnectWebSocket,
	getChatUsers,
	sendJoinMessage,
	sendMessageWebSocket,
	subscribeToChat,
	subscribeToUserQueue,
} from '@/services/chat'
import { postDoc } from '@/services/uploadFiles'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from 'next/image'
import React, { useCallback, useEffect, useRef, useState } from 'react'
import toast from 'react-hot-toast'
import { FaArrowLeft, FaRegTrashAlt } from 'react-icons/fa'
import { IoInformationCircleOutline, IoSendSharp } from 'react-icons/io5'
import { TfiClip } from 'react-icons/tfi'
import * as Stomp from 'stompjs'
import { IChat } from '../../Chat.interface'
import { Item } from '../../Chat.styled'
import { ContextMenu } from '../ContextMenu/ContextMenu'
import {
	ChatInput,
	ChatMainBody,
	ChatMainContainer,
	ChatMainHeader,
	EmptyMessage,
	Message,
} from './ChatMain.styled'

interface IMessage {
	id: string | number
	chatId: number
	senderId: number | string
	content: string
	timestamp: string
	edited: boolean
	editedTimestamp: string | null
	readBy: (number | string)[] | null
	attachments: Attachment[]
}

interface ChatProps {
	setUsers: React.Dispatch<React.SetStateAction<IChat[]>>
	setChatOpen: React.Dispatch<React.SetStateAction<boolean>>
	isChatOpen: boolean
}

export const ChatMain = ({ setUsers, setChatOpen, isChatOpen }: ChatProps) => {
	const { chatUser, setChatUser, chatId, user } = useCatalog()
	const [messages, setMessages] = useState<IMessage[]>([])
	const [message, setMessage] = useState('')
	const [attachments, setAttachments] = useState<Attachment[]>([])
	const [error, setError] = useState('')
	const [previewImageUrl, setPreviewImageUrl] = useState<string | null>(null)
	const [contextMenu, setContextMenu] = useState<{
		x: number
		y: number
		messageId: string | number
	} | null>(null)

	const handleImageClick = (url: string) => setPreviewImageUrl(url)
	const closePreview = () => setPreviewImageUrl(null)
	const { t } = useTranslation('common')
	const fileInputRef = useRef<HTMLInputElement>(null)
	const chatBodyRef = useRef<HTMLDivElement>(null)

	const scrollToBottom = useCallback(() => {
		if (chatBodyRef.current) {
			chatBodyRef.current.scrollTop = chatBodyRef.current.scrollHeight
		}
	}, [])

	const onMessageReceived = useCallback(
		(msg: ChatMessageWebSocketDTO) => {
			setMessages(prevMessages => {
				if (prevMessages.some(m => m.id === msg.id)) {
					return prevMessages
				}
				return [...prevMessages, msg as IMessage]
			})
			scrollToBottom()
		},
		[scrollToBottom]
	)

	const onPrivateMessageReceived = useCallback(
		(msg: ChatMessageWebSocketDTO) => {
			console.log('Private message received:', msg)
		},
		[]
	)

	const onWebSocketConnected = useCallback(() => {
		if (chatId && user) {
			subscribeToChat(chatId)
			subscribeToUserQueue()
			sendJoinMessage(chatId, user.id)
		}
	}, [chatId, user])

	const onWebSocketError = useCallback(
		(err: Stomp.Frame | string) => {
			console.error('WebSocket Error:', err)
		},
		[t]
	)

	useEffect(() => {
		if (chatUser && Array.isArray(chatUser.content)) {
			setMessages(chatUser.content)
			scrollToBottom()
		} else {
			setMessages([])
		}
	}, [chatUser, scrollToBottom])

	useEffect(() => {
		const fetchUsers = async () => {
			try {
				const response = await getChatUsers()
				setUsers(response)
			} catch (error) {
				console.error(t('chatMain.errorFetchingUsers'), error)
			}
		}

		fetchUsers()
	}, [setUsers, t])

	useEffect(() => {
		if (chatId && user) {
			const accessToken = localStorage.getItem('accessToken')
			if (accessToken) {
				connectWebSocket(
					accessToken,
					onWebSocketConnected,
					onWebSocketError,
					onMessageReceived,
					onPrivateMessageReceived
				)
			} else {
				console.error('No access token found for WebSocket connection.')
			}
		}

		return () => {
			disconnectWebSocket()
		}
	}, [
		chatId,
		user,
		onWebSocketConnected,
		onWebSocketError,
		onMessageReceived,
		onPrivateMessageReceived,
	])

	useEffect(() => {
		scrollToBottom()
	}, [messages, scrollToBottom])

	const handlePostMessage = async () => {
		if ((!message.trim() && attachments.length === 0) || !chatId || !user)
			return

		try {
			const payload: ChatMessageWebSocketDTO = {
				chatId,
				content: message.trim(),
				attachments,
				senderId: user.id,
				timestamp: new Date().toISOString(),
			}

			sendMessageWebSocket(payload)

			setMessage('')
			setAttachments([])
		} catch (error) {
			toast.error(t('chatMain.sendMessageError'))
			console.error(error)
		}
	}

	const handleFileSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
		const file = e.target.files?.[0]
		if (!file) return

		let type: 'IMAGE' | 'VIDEO' | 'DOCUMENT' = 'DOCUMENT'
		if (file.type.startsWith('image/')) type = 'IMAGE'
		else if (file.type.startsWith('video/')) type = 'VIDEO'

		try {
			const url = await postDoc(file)

			setAttachments(prev => [...prev, { url, type }])
			setError('')
			console.error(error)
		} catch (err) {
			console.error(t('chatMain.uploadError'), err)
			toast.error(t('chatMain.fileUploadError'))
			setError(t('chatMain.fileUploadError'))
		}

		e.target.value = ''
	}

	const triggerFileInput = () => {
		fileInputRef.current?.click()
	}

	const handleBackArrowClick = () => {
		setChatUser(null)
		setChatOpen(false)
	}

	const handleDeleteChat = async () => {
		if (!chatId) return

		try {
			await deleteChat(chatId)
			setUsers(prevUsers =>
				prevUsers.filter(userItem => userItem.id !== chatId)
			)
			setChatUser(null)
			toast.success(t('chatMain.chatDeleted'))
		} catch (error) {
			console.error(t('chatMain.deleteChatError'), error)
			toast.error(t('chatMain.deleteChatError'))
		}
	}

	const handleDeleteMessage = async (messageId: number | string) => {
		if (!chatId || !messages?.length) return

		try {
			await deleteMessage(messageId)

			setMessages(prevMessages =>
				prevMessages.filter(msg => msg.id !== messageId)
			)

			toast.success(t('chatMain.messageDeleted'))
		} catch (error) {
			console.error(t('chatMain.deleteMessageError'), error)
			toast.error(t('chatMain.deleteMessageError'))
		}
	}

	const handleContextMenu = (
		e: React.MouseEvent,
		messageId: string | number
	) => {
		e.preventDefault()
		setContextMenu({
			x: e.clientX,
			y: e.clientY,
			messageId,
		})
	}

	const handleCloseContextMenu = () => {
		setContextMenu(null)
	}

	const isEmpty = messages.length === 0

	return (
		<ChatMainContainer isChatOpen={isChatOpen}>
			<ChatMainHeader>
				<div className='flex items-center gap-x-[18px]'>
					<FaArrowLeft
						className='cursor-pointer'
						onClick={handleBackArrowClick}
					/>
					{chatUser ? (
						<Item>
							<Image
								width={60}
								height={60}
								alt={chatUser.name || 'user'}
								src={chatUser.chatImageUrl || '/images/user/altUser.png'}
								unoptimized
							/>
							<div>
								<h4>{chatUser.name}</h4>
								<span>ID: {chatUser.id}</span>
							</div>
						</Item>
					) : (
						<div>{t('chatMain.selectChat')}</div>
					)}
				</div>
				<div className='flex items-center gap-x-[15px] cursor-pointer'>
					<FaRegTrashAlt onClick={handleDeleteChat} />
					<IoInformationCircleOutline size={24} />
				</div>
			</ChatMainHeader>

			<ChatMainBody id='chat-main-body' ref={chatBodyRef}>
				{isEmpty ? (
					<EmptyMessage>{t('chatMain.emptyMessages')}</EmptyMessage>
				) : (
					messages?.map?.(msg => {
						const isOwn = msg.senderId === user?.id

						return (
							<div
								key={msg.id}
								className={`flex items-start gap-x-[12px] ${
									isOwn ? 'justify-end flex-row-reverse' : ''
								}`}
								onContextMenu={e => isOwn && handleContextMenu(e, msg.id)}
							>
								<Image
									src={
										(isOwn
											? user?.profilePictureUrl
											: chatUser?.chatImageUrl) || '/images/user/altUser.png'
									}
									alt='userLogo'
									width={48}
									height={48}
									className='rounded-[50%] max-w-[48px] max-h-[48px] min-w-[48px] min-h-[48px]'
									unoptimized
								/>
								<Message
									background={isOwn ? colors.btnMainColor : undefined}
									color={isOwn ? colors.mainWhiteTextColor : undefined}
									className={isOwn ? 'ml-auto' : ''}
								>
									{msg.content && <p>{msg.content}</p>}

									<div className='mt-2 grid grid-cols-1 gap-3'>
										{msg.attachments?.map?.((att, i) => {
											if (att.type === 'IMAGE') {
												return (
													<div
														key={i}
														className='relative w-[150px] h-[150px] cursor-pointer'
														onClick={() => handleImageClick(att.url)}
													>
														<Image
															src={att.url}
															alt='image'
															fill
															objectFit='cover'
															className='rounded-lg shadow-md hover:brightness-75 transition '
															unoptimized
														/>
													</div>
												)
											}
											if (att.type === 'DOCUMENT') {
												return (
													<div
														key={i}
														className='flex items-center gap-2 px-3 py-2 bg-white rounded-md shadow-md'
													>
														📄{' '}
														<a
															href={att.url}
															target='_blank'
															rel='noopener noreferrer'
															className='underline text-blue-600'
														>
															{t('chatMain.documentText')}
														</a>
													</div>
												)
											}
											if (att.type === 'VIDEO') {
												return (
													<div key={i} className='w-full max-w-[250px]'>
														<video
															controls
															className='rounded-md shadow-md w-full'
														>
															<source src={att.url} type='video/mp4' />
															{t('chatMain.videoNotSupportedShort')}
														</video>
													</div>
												)
											}
											return null
										})}
									</div>
									<span className='text-[12px] font-[700] shadow-2xs'>
										{new Date(msg.timestamp ?? '').toLocaleDateString() || ''}
									</span>
								</Message>
							</div>
						)
					})
				)}
			</ChatMainBody>

			{chatUser && (
				<div className='flex flex-col p-4 border-t border-gray-200'>
					{attachments.length > 0 && (
						<div className='flex flex-wrap gap-4 mb-4 p-2 bg-gray-50 rounded-lg'>
							{attachments.map((att, index) => {
								if (att.type === 'IMAGE') {
									return (
										<div key={index} className='relative w-24 h-24'>
											<Image
												src={att.url}
												alt='attachment'
												layout='fill'
												objectFit='cover'
												className='rounded-md'
												unoptimized
											/>
											<button
												className='absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-xs'
												onClick={() =>
													setAttachments(prev =>
														prev.filter((_, i) => i !== index)
													)
												}
											>
												✕
											</button>
										</div>
									)
								}
								if (att.type === 'DOCUMENT') {
									return (
										<div
											key={index}
											className='flex items-center gap-2 px-3 py-2 bg-gray-100 rounded-md relative'
										>
											📄{' '}
											<a
												href={att.url}
												target='_blank'
												rel='noopener noreferrer'
												className='text-sm underline'
											>
												{t('chatMain.documentText')}
											</a>
											<button
												className='absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-xs'
												onClick={() =>
													setAttachments(prev =>
														prev.filter((_, i) => i !== index)
													)
												}
											>
												✕
											</button>
										</div>
									)
								}
								if (att.type === 'VIDEO') {
									return (
										<div key={index} className='w-40 h-24 relative'>
											<video
												controls
												className='rounded-md w-full h-full object-cover'
											>
												<source src={att.url} type='video/mp4' />
												{t('chatMain.videoNotSupported')}
											</video>
											<button
												className='absolute -top-2 -right-2 bg-red-500 text-white rounded-full w-6 h-6 flex items-center justify-center text-xs'
												onClick={() =>
													setAttachments(prev =>
														prev.filter((_, i) => i !== index)
													)
												}
											>
												✕
											</button>
										</div>
									)
								}
								return null
							})}
						</div>
					)}

					<div className='flex relative items-center justify-between'>
						<ChatInput
							type='text'
							placeholder={t('chatMain.messageInputPlaceholder')}
							value={message}
							onChange={e => setMessage(e.target.value)}
							onKeyPress={e => {
								if (e.key === 'Enter') {
									e.preventDefault()
									handlePostMessage()
								}
							}}
						/>

						<div className='flex items-center gap-x-[10px] absolute right-[5%] cursor-pointer'>
							<TfiClip size={24} onClick={triggerFileInput} />
							<input
								type='file'
								accept='*/*'
								ref={fileInputRef}
								onChange={handleFileSelect}
								className='hidden'
							/>

							<button
								className='bg-[#007BFF] rounded-[50%] p-[10px]'
								onClick={handlePostMessage}
							>
								<IoSendSharp color='#fff' size={21} />
							</button>
						</div>
					</div>
				</div>
			)}
			{previewImageUrl && (
				<div
					onClick={closePreview}
					className='fixed inset-0 bg-black bg-opacity-70 flex justify-center items-center z-[1000] '
				>
					<div className='relative max-w-3xl w-full max-h-[90%] overflow-auto p-4'>
						<Image
							src={previewImageUrl}
							alt='preview'
							layout='responsive'
							width={800}
							height={600}
							className='rounded-lg shadow-lg object-contain '
							unoptimized
						/>
						<button
							onClick={closePreview}
							className='absolute top-2 right-2 bg-white rounded-full p-2 shadow-md'
						>
							✕
						</button>
					</div>
				</div>
			)}
			{contextMenu && (
				<ContextMenu
					x={contextMenu.x}
					y={contextMenu.y}
					onDelete={() => handleDeleteMessage(contextMenu.messageId)}
					onClose={handleCloseContextMenu}
				/>
			)}
		</ChatMainContainer>
	)
}
