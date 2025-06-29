import { colors } from '@/utils'
import { useCheckAdmin } from '@/utils/useCheckAdmin'
import { useTranslation } from 'next-i18next'
import { useEffect, useState } from 'react'
import { RiMenuUnfold2Fill } from 'react-icons/ri'
import Administration from '../Administration/Administration'
import { useCatalog } from '../Catalog/CatalogContext/CatalogContext'
import { FavoriteTab } from '../Favorite/Favorite'
import { useDevice } from '../hooks/useDevice'
import Profile from '../Profile/Profile'
import { ContentTab } from '../Profile/ProfileTabs/Content/Content'
import { ListingTab } from '../Profile/ProfileTabs/Listings/Listings'
import { Sidebar } from '../SideBar/SideBar'
import {
	MenuContent,
	MobileMenu,
	MobileMenuWrapper,
	ProfilePageWrapper,
} from './ProfilePage.styled'

export const ProfilePage = () => {
	const { activeTab, setActiveTab } = useCatalog()
	const { isDesktop, isLargeTablet } = useDevice()
	const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)
	const { t } = useTranslation('common')
	const isAdmin = useCheckAdmin()
	useEffect(() => {
		const prev = sessionStorage.getItem('previousPath')

		if (window.location.pathname === '/profile' && prev?.includes('/admin')) {
			setActiveTab(0)
			sessionStorage.removeItem('previousPath')
		}
	}, [setActiveTab])

	if (isAdmin === null) {
		return <p>Loading...</p>
	}

	const toggleMobileMenu = () => {
		setIsMobileMenuOpen(!isMobileMenuOpen)
	}

	return (
		<ProfilePageWrapper>
			{isLargeTablet || isDesktop ? (
				<Sidebar
					setActiveTab={setActiveTab}
					activeTab={activeTab}
					isAdmin={isAdmin}
				/>
			) : (
				<>
					<div
						style={{
							position: 'absolute',
							display: 'flex',
							alignItems: 'center',
							gap: '5px',
							top: '13%',
							left: '1%',
							zIndex: 998,
							cursor: 'pointer',
						}}
						onClick={toggleMobileMenu}
					>
						<RiMenuUnfold2Fill
							size={36}
							color={colors.btnMainColor}
							style={{
								backgroundColor: colors.mainWhiteTextColor,
								borderRadius: '50%',
								padding: '5px',
							}}
						/>
						<span className='text-[18px] tracking-[1px] font-bold'>
							{t('common.menu')}
						</span>
					</div>
					{isMobileMenuOpen && (
						<MobileMenuWrapper onClick={toggleMobileMenu}>
							<MobileMenu onClick={e => e.stopPropagation()}>
								<MenuContent>
									<Sidebar
										isAdmin={isAdmin}
										setActiveTab={setActiveTab}
										activeTab={activeTab}
									/>
								</MenuContent>
							</MobileMenu>
						</MobileMenuWrapper>
					)}
				</>
			)}
			{activeTab === 0 && <Profile />}
			{activeTab === 1 && <FavoriteTab />}
			{activeTab === 2 && <ListingTab />}
			{activeTab === 3 && isAdmin && <ContentTab />}
			{activeTab === 4 && isAdmin && <Administration />}
			{/* {activeTab === 3 && <SubscriptionTab />} */}
		</ProfilePageWrapper>
	)
}
