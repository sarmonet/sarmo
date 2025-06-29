import { getAllUsers, putNewUserRole } from '@/services/administration'
import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { MdOutlineVerified } from 'react-icons/md'
import { RxDotsHorizontal } from 'react-icons/rx'
import UserSearch from '../AdministrationSearch'
import { IUser } from './Admin.interface'
import {
	AdminAccBlock,
	AdminAccBlocks,
	AdminAccContent,
	AdminAccTitle,
	AdminAccTop,
	AdminAccWrapper,
	AdminRoleChange,
} from './AdministrationBodyContent.styled'

export const AdministrationAcc = () => {
	const [users, setUsers] = useState<IUser[]>([])
	const [, setActive] = useState(false)
	const [activeId, setActiveId] = useState<number>(0)

	const { t } = useTranslation()

	useEffect(() => {
		const fetchUsers = async () => {
			try {
				const response = await getAllUsers()
				setUsers(response)
			} catch (error) {
				console.error(`${t('administrationAcc.errorFetchingUsers')} `, error)
			}
		}

		fetchUsers()
	}, [t])

	const handleRoleChange = async (userId: number, roleName: number) => {
		try {
			const updatedUser = await putNewUserRole({ id: userId, name: roleName })
			setUsers(prevUsers =>
				prevUsers.map(user =>
					user.id === userId ? { ...user, ...updatedUser } : user
				)
			)
		} catch (error) {
			console.error(`${t('administrationAcc.errorUpdatingUserRole')} `, error)
		} finally {
			setActive(false)
			setActiveId(0)
		}
	}

	const handleStatusChange = async (userId: number, status: number) => {
		try {
			const updatedUser = await putNewUserRole({ id: userId, name: status })
			setUsers(prevUsers =>
				prevUsers.map(user =>
					user.id === userId ? { ...user, ...updatedUser } : user
				)
			)
		} catch (error) {
			console.error(`${t('administrationAcc.errorUpdatingUserStatus')} `, error)
		} finally {
			setActive(false)
			setActiveId(0)
		}
	}

	const handleSetActive = (id: number) => {
		if (activeId === id) {
			setActive(false)
			setActiveId(0)
		} else {
			setActive(true)
			setActiveId(id)
		}
	}

	return (
		<AdminAccWrapper>
			<UserSearch users={users} />
			<AdminAccTop>
				<AdminAccTitle>{t('administrationAcc.id')}</AdminAccTitle>
				<AdminAccTitle>{t('administrationAcc.username')}</AdminAccTitle>
				<AdminAccTitle>{t('administrationAcc.role')}</AdminAccTitle>
			</AdminAccTop>
			<AdminAccBlocks>
				{users.map(user => {
					return (
						<AdminAccBlock key={user.id}>
							<AdminAccContent>
								<span>{user.id}</span>{' '}
							</AdminAccContent>
							<AdminAccContent>
								<h3>{user.name}</h3>
							</AdminAccContent>

							<AdminAccContent>
								<span>{user.roles[0].name}</span>
							</AdminAccContent>
							<div style={{ position: 'relative' }}>
								<button
									onClick={() => {
										handleSetActive(user.id)
									}}
								>
									<RxDotsHorizontal size={24} />
								</button>
								{activeId === user.id && (
									<AdminRoleChange>
										<button onClick={() => handleRoleChange(user.id, 2)}>
											<MdOutlineVerified size={18} /> ADMIN
										</button>
										<button onClick={() => handleRoleChange(user.id, 3)}>
											<MdOutlineVerified size={18} /> MODERATOR
										</button>
										<button onClick={() => handleStatusChange(user.id, 4)}>
											<MdOutlineVerified size={18} />{' '}
											{t('administrationAcc.verify')}
										</button>
									</AdminRoleChange>
								)}
							</div>
						</AdminAccBlock>
					)
				})}
			</AdminAccBlocks>
		</AdminAccWrapper>
	)
}
