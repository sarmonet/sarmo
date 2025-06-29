import { Title } from '@/components/Title/Title'
import { useTranslation } from 'next-i18next'
import { useState } from 'react'
import { ProfileMain, ProfileWrapper } from '../../Profile.styled'
import { ListingMain } from './ListingsTabs/ListingMain'
import { ListingSideBar } from './ListingsTabs/ListingSideBar'
export const ListingTab = () => {
	const [activeTab, setActiveTab] = useState(0)
	const { t } = useTranslation('common')
	return (
		<ProfileWrapper>
			<Title>{t('sideProfile.listings')}</Title>
			<ProfileMain>
				<ListingSideBar setActiveTab={setActiveTab} activeTab={activeTab} />
				<ListingMain activeTab={activeTab} />
			</ProfileMain>
		</ProfileWrapper>
	)
}
