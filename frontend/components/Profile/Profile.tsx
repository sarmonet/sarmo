import withAuth from '@/hoc/withAuth'
import { useTranslation } from 'next-i18next'
import { useState } from 'react'
import { Title } from '../Title/Title'
import { ProfileMain, ProfileWrapper } from './Profile.styled'
import { ProfileContent } from './ProfileContent/ProfileContent'
import { ProfileSideBar } from './ProfileSideBar/ProfileSideBar'
const Profile = () => {
	const [activeTab, setActiveTab] = useState(0)
	const { t } = useTranslation('common')
	return(
		<ProfileWrapper>
			<Title>{t('profilePage.profileTitle')}</Title>
			<ProfileMain>
				<ProfileSideBar setActiveTab={setActiveTab} activeTab={activeTab}/>
				<ProfileContent activeTab={activeTab}/>
			</ProfileMain>
		</ProfileWrapper>
	)
}
export default withAuth(Profile)
