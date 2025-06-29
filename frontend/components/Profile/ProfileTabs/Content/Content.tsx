import { Title } from '@/components/Title/Title'
import { useTranslation } from 'next-i18next'
import { useState } from 'react'
import { ProfileMain, ProfileWrapper } from '../../Profile.styled'
import { ContentMain } from './ContentComponents/ContentMain'
import { ContentSideBar } from './ContentComponents/ContentSideBar/ContentSideBar'
export const ContentTab = () => {
	const [activeTab, setActiveTab] = useState(0)
	const { t } = useTranslation('common')
	return (
		<ProfileWrapper>
			<Title>{t('sideProfile.blog')}</Title>
			<ProfileMain>
				<ContentSideBar setActiveTab={setActiveTab} activeTab={activeTab} />
				<ContentMain activeTab={activeTab} />
			</ProfileMain>
		</ProfileWrapper>
	)
}
