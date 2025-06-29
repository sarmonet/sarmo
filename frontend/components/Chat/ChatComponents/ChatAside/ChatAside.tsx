import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { getChatUsers, openChat } from '@/services/chat'
import Image from "next/legacy/image"
import router from 'next/router'
import { useEffect, useState } from 'react'
import { IChat } from '../../Chat.interface'
import { Item } from '../../Chat.styled'
import { UserSearch } from '../../ChatComponents/UserSearch'
import { Aside, AsideItems } from './ChatAside.styled'

export interface ChatProps {
	users: IChat[]
	setUsers: React.Dispatch<React.SetStateAction<IChat[]>>
	setIsChatOpen: React.Dispatch<React.SetStateAction<boolean>>
	isChatOpen: boolean
}

export const ChatAside = ({
	setUsers,
	users,
	isChatOpen,
	setIsChatOpen,
}: ChatProps) => {
	const { setChatUser, setChatId, user } = useCatalog()
	const urlChatId = Number(router.query.chatId)

	const [searchQuery, setSearchQuery] = useState('')

	useEffect(() => {
		const fetchUsers = async () => {
			try {
				const response = await getChatUsers()
				setUsers(response)
			} catch (error) {
				console.error('❌ Ошибка при получении пользователей:', error)
			}
		}

		fetchUsers()
	}, [setUsers])

	useEffect(() => {
		if (urlChatId > 0 && users.length > 0 && user) {
			const userToOpenChat = users.find(
				chat =>
					chat.userIds &&
					chat.userIds.includes(urlChatId) &&
					chat.userIds.includes(user.id)
			)

			if (userToOpenChat) {
				handleChat(userToOpenChat.id)
			}
		}
	}, [urlChatId, users, user])

	const handleChat = async (chatId: number) => {
		try {
			const response = await openChat(chatId)
			const selectedUser = users.find(user => user.id === chatId)
			if (!selectedUser) return

			setChatUser({
				id: chatId,
				name: selectedUser.name,
				chatImageUrl: selectedUser.chatImageUrl,
				creatorId: selectedUser.creatorId,
				content: response,
				userIds: selectedUser.userIds,
			})

			setChatId(chatId)
			setIsChatOpen(true)
		} catch (error) {
			console.error('❌ Ошибка при получении чата:', error)
		}
	}

	const filteredUsers = users.filter(
		user =>
			user.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
			String(user.id).includes(searchQuery)
	)

	return (
		<Aside isChatOpen={isChatOpen}>
			<AsideItems>
				<UserSearch searchQuery={searchQuery} setSearchQuery={setSearchQuery} />

				{filteredUsers.map(user => (
					<Item key={user.id} onClick={() => handleChat(user.id)}>
						<Image
							src={user.chatImageUrl || '/images/user/altUser.png'}
							alt='aside1'
							width={60}
							height={60}
							unoptimized
						/>
						<div>
							<h4>{user.name}</h4>
							<span>ID: {user.id}</span>
						</div>
					</Item>
				))}
			</AsideItems>
		</Aside>
	)
}
