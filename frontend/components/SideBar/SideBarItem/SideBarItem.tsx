import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Link from 'next/link'
import { useRouter } from 'next/router'
import { CiLogout } from 'react-icons/ci'
import { GetSideBar } from '../SideBar.data'
import { LogOut, SideItem, SideItems } from './SideBarItem.styled'
interface Props {
	setActiveTab: (index: number) => void
	activeTab: number
	isAdmin?: boolean
}

export const SideBarItem = ({ setActiveTab, activeTab, isAdmin }: Props) => {
	const router = useRouter()
	const { t } = useTranslation('common')
	const { setUser } = useCatalog()
	const SideBar = GetSideBar(t)
	const removeItem = (key: string) => {
		localStorage.removeItem(key)
		router.push('/profile')
	}
	const handleLogout = () => {
		removeItem('accessToken')
		router.push('/login')
		setUser(null)
	}
	return (
		<>
			<SideItems>
				{SideBar.filter(item => isAdmin || !item.adminOnly).map(
					(item, index) => (
						<SideItem
							key={index}
							onClick={() => setActiveTab(index)}
							style={
								activeTab === index
									? { transform: 'translateX(5%)', color: colors.btnMainColor }
									: {}
							}
						>
							{item.link ? (
								<Link
									href={item.link}
									style={{
										display: 'flex',
										alignItems: 'center',
										columnGap: '15px',
									}}
								>
									{item.icon} <span>{item.title}</span>
								</Link>
							) : (
								<>
									{item.icon} <span>{item.title}</span>
								</>
							)}
						</SideItem>
					)
				)}
				<LogOut
					onClick={() => {
						handleLogout()
					}}
				>
					<CiLogout size={23} /> <span>{t('sideProfile.logout')}</span>{' '}
				</LogOut>
			</SideItems>
		</>
	)
}
