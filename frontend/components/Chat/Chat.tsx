import withAuth from '@/hoc/withAuth'
import { useState } from 'react'
import { IChat } from './Chat.interface'
import { ChatWrapper } from './Chat.styled'
import { ChatAside } from './ChatComponents/ChatAside/ChatAside'
import { ChatMain } from './ChatComponents/ChatMain/ChatMain'

const Chat = () => {
	const [users, setUsers] = useState<IChat[]>([])

	const [isChatOpen, setIsChatOpen] = useState(false)

	return (
		<ChatWrapper>
			<ChatAside
				setIsChatOpen={setIsChatOpen}
				isChatOpen={isChatOpen}
				setUsers={setUsers}
				users={users}
			/>
			<ChatMain
				setChatOpen={setIsChatOpen}
				isChatOpen={isChatOpen}
				setUsers={setUsers}
			/>
		</ChatWrapper>
	)
}
export default withAuth(Chat)
