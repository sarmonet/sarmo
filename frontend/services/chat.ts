'use client'

import { axiosInstanceChat } from '@/utils/axiosInstance'
import { Client, Frame, IMessage } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

export type Attachment = {
	url: string
	type: 'IMAGE' | 'VIDEO' | 'DOCUMENT'
}

interface PostMessageRestPayload {
	chatId: number
	content: string
	attachments?: Attachment[]
}

export interface ChatMessageWebSocketDTO {
	id?: string
	chatId: number
	senderId?: number
	content: string
	timestamp?: string
	type?: 'CHAT' | 'JOIN' | 'LEAVE'
	attachments?: Attachment[]
}

export const getChatUsers = async () => {
	try {
		const response = await axiosInstanceChat.get('/my', {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				'Content-Type': 'application/json',
			},
		})
		return response.data
	} catch (error) {
		console.error('❌ Ошибка при получении пользователей чата:', error)
		throw error
	}
}

export const getChat = async (id: number) => {
	try {
		const response = await axiosInstanceChat.get(`/${id}`, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				'Content-Type': 'application/json',
			},
		})
		return response.data
	} catch (error) {
		console.error('❌ Ошибка при получении информации о чате:', error)
		throw error
	}
}

export const createChat = async (participantIds: number) => {
	try {
		const response = await axiosInstanceChat.post(
			``,
			{
				participantIds: [participantIds],
				type: 'PERSONAL',
			},
			{
				headers: {
					Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
					'Content-Type': 'application/json',
				},
			}
		)
		return response.data
	} catch (error) {
		console.error('❌ Ошибка при создании чата:', error)
		throw error
	}
}

export const openChat = async (chatId: number) => {
	try {
		const response = await axiosInstanceChat.get(`/${chatId}/messages`, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				'Content-Type': 'application/json',
			},
		})
		return response.data
	} catch (error) {
		console.error(
			'❌ Ошибка при открытии чата/получении истории сообщений:',
			error
		)
		throw error
	}
}

export const postMessageRest = async (payload: PostMessageRestPayload) => {
	try {
		const response = await axiosInstanceChat.post(`/message`, payload, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				'Content-Type': 'application/json',
			},
		})
		return response.data
	} catch (error) {
		console.error('❌ Ошибка при отправке сообщения через REST:', error)
		throw error
	}
}

export const deleteChat = async (chatId: number) => {
	try {
		const response = await axiosInstanceChat.delete(`/${chatId}`, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				'Content-Type': 'application/json',
			},
		})
		return response.data
	} catch (error) {
		console.error('❌ Ошибка при удалении чата:', error)
		throw error
	}
}

export const deleteMessage = async (messageId: number | string) => {
	try {
		const response = await axiosInstanceChat.delete(`/message/${messageId}`, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				'Content-Type': 'application/json',
			},
		})
		return response.data
	} catch (error) {
		console.error('❌ Ошибка при удалении чата:', error)
		throw error
	}
}

let stompClient: Client | null = null
let reconnectAttempts = 0
const MAX_RECONNECT_ATTEMPTS = 5
const RECONNECT_DELAY_MS = 3000

let onMessageReceivedCallback:
	| ((message: ChatMessageWebSocketDTO) => void)
	| null = null
let onPrivateMessageReceivedCallback:
	| ((message: ChatMessageWebSocketDTO) => void)
	| null = null
let onWebSocketConnectedCallback: (() => void) | null = null
let onWebSocketErrorCallback: ((error: Frame | string) => void) | null = null

/**
 * @param token
 * @param onConnectCallback
 * @param onErrorCallback
 * @param onMsgReceived
 * @param onPrivMsgReceived
 */
export const connectWebSocket = (
	token: string,
	onConnectCallback: () => void,
	onErrorCallback: (error: Frame | string) => void,
	onMsgReceived: (message: ChatMessageWebSocketDTO) => void,
	onPrivMsgReceived: (message: ChatMessageWebSocketDTO) => void
) => {
	onWebSocketConnectedCallback = onConnectCallback
	onWebSocketErrorCallback = onErrorCallback
	onMessageReceivedCallback = onMsgReceived
	onPrivateMessageReceivedCallback = onPrivMsgReceived

	if (stompClient && stompClient.active) {
		stompClient.deactivate()
	}

	stompClient = new Client({
		webSocketFactory: () => new SockJS('https://sarmo.net/ws'),
		connectHeaders: {
			Authorization: `Bearer ${token}`,
		},
		onConnect: onConnected,
		onStompError: onError,
		onWebSocketError: e => onError(e.message),
	})
	stompClient.activate()
}

function onConnected(frame: Frame) {
	console.log('Connected to WebSocket:', frame)
	reconnectAttempts = 0
	onWebSocketConnectedCallback?.()
}

function onError(error: Frame | string) {
	console.error('WebSocket connection error:', error)
	onWebSocketErrorCallback?.(error)

	if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
		reconnectAttempts++
		console.log(
			`Attempting to reconnect (${reconnectAttempts}/${MAX_RECONNECT_ATTEMPTS})...`
		)
		setTimeout(() => {
			const token = localStorage.getItem('accessToken')
			if (token) {
				if (
					onWebSocketConnectedCallback &&
					onWebSocketErrorCallback &&
					onMessageReceivedCallback &&
					onPrivateMessageReceivedCallback
				) {
					connectWebSocket(
						token,
						onWebSocketConnectedCallback,
						onWebSocketErrorCallback,
						onMessageReceivedCallback,
						onPrivateMessageReceivedCallback
					)
				}
			} else {
				console.error('No access token available for reconnecting WebSocket.')
			}
		}, RECONNECT_DELAY_MS)
	} else {
		console.error(
			'Max reconnect attempts reached. Could not establish WebSocket connection.'
		)
	}
}

/**
 * Подписывается на топик чата.
 * @param chatId ID чата.
 */
export const subscribeToChat = (chatId: number) => {
	if (stompClient && stompClient.connected) {
		stompClient.subscribe(`/topic/chat/${chatId}`, (message: IMessage) => {
			const chatMsg: ChatMessageWebSocketDTO = JSON.parse(message.body)
			onMessageReceivedCallback?.(chatMsg)
		})
	} else {
		console.warn('STOMP client not connected. Cannot subscribe to chat.')
	}
}

export const subscribeToUserQueue = () => {
	if (stompClient && stompClient.connected) {
		stompClient.subscribe(`/user/queue`, (message: IMessage) => {
			const chatMsg: ChatMessageWebSocketDTO = JSON.parse(message.body)
			onPrivateMessageReceivedCallback?.(chatMsg)
		})
	} else {
		console.warn('STOMP client not connected. Cannot subscribe to user queue.')
	}
}

/**
 * @param chatId
 * @param currentUserId
 */
export const sendJoinMessage = (chatId: number, currentUserId: number) => {
	if (stompClient && stompClient.connected) {
		const chatMessage: ChatMessageWebSocketDTO = {
			chatId: chatId,
			senderId: currentUserId,
			content: `User ${currentUserId} joined!`,
			type: 'JOIN',
		}
		stompClient.publish({
			destination: '/app/chat.addUser',
			body: JSON.stringify(chatMessage),
		})
	} else {
		console.warn('STOMP client not connected. Cannot send join message.')
	}
}

/**
 * @param payload Объект сообщения с chatId и content.
 */
export const sendMessageWebSocket = (payload: {
	chatId: number
	content: string
	attachments?: Attachment[]
}) => {
	if (stompClient && stompClient.connected) {
		const chatMessage: ChatMessageWebSocketDTO = {
			chatId: payload.chatId,
			content: payload.content,
			attachments: payload.attachments,
			type: 'CHAT',
		}
		stompClient.publish({
			destination: '/app/chat.sendMessage',
			body: JSON.stringify(chatMessage),
		})
	} else {
		console.warn(
			'STOMP client not connected. Cannot send message via WebSocket.'
		)
	}
}

export const disconnectWebSocket = () => {
	if (stompClient) {
		stompClient.deactivate()
		console.log('Disconnected from WebSocket.')
		stompClient = null
		reconnectAttempts = 0
	}
}

if (typeof window !== 'undefined') {
	window.addEventListener('beforeunload', disconnectWebSocket)
}
